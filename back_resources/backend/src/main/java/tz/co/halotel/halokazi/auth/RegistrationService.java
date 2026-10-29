package tz.co.halotel.halokazi.auth;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tz.co.halotel.halokazi.audit.AuditAction;
import tz.co.halotel.halokazi.audit.AuditResult;
import tz.co.halotel.halokazi.audit.AuditService;
import tz.co.halotel.halokazi.auth.dto.AuthResponse;
import tz.co.halotel.halokazi.auth.dto.RegisterRequest;
import tz.co.halotel.halokazi.auth.dto.RegisterResponse;
import tz.co.halotel.halokazi.auth.dto.ResendOtpRequest;
import tz.co.halotel.halokazi.auth.dto.ResendOtpResponse;
import tz.co.halotel.halokazi.auth.dto.VerifyOtpRequest;
import tz.co.halotel.halokazi.common.config.HalokaziProperties;
import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.BusinessTransaction;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.common.util.Emails;
import tz.co.halotel.halokazi.common.util.PhoneNumbers;
import tz.co.halotel.halokazi.common.web.ClientInfo;
import tz.co.halotel.halokazi.employer.Employer;
import tz.co.halotel.halokazi.employer.EmployerRepository;
import tz.co.halotel.halokazi.jobseeker.JobSeekerProfile;
import tz.co.halotel.halokazi.jobseeker.JobSeekerProfileRepository;
import tz.co.halotel.halokazi.otp.OtpDispatch;
import tz.co.halotel.halokazi.otp.OtpPurpose;
import tz.co.halotel.halokazi.otp.OtpService;
import tz.co.halotel.halokazi.settings.Setting;
import tz.co.halotel.halokazi.settings.SettingsService;
import tz.co.halotel.halokazi.user.AppUser;
import tz.co.halotel.halokazi.user.AppUserRepository;
import tz.co.halotel.halokazi.user.Role;

/** Self-registration and SMS phone verification (AUTH-01, AUTH-02, AUTH-03, AUTH-07). */
@Service
@BusinessTransaction
public class RegistrationService {

    private final AppUserRepository users;
    private final JobSeekerProfileRepository profiles;
    private final EmployerRepository employers;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otps;
    private final AuthService auth;
    private final AuditService audit;
    private final SettingsService settings;
    private final HalokaziProperties properties;
    private final Clock clock;

    public RegistrationService(AppUserRepository users, JobSeekerProfileRepository profiles,
            EmployerRepository employers, PasswordEncoder passwordEncoder, OtpService otps, AuthService auth,
            AuditService audit, SettingsService settings, HalokaziProperties properties, Clock clock) {
        this.users = users;
        this.profiles = profiles;
        this.employers = employers;
        this.passwordEncoder = passwordEncoder;
        this.otps = otps;
        this.auth = auth;
        this.audit = audit;
        this.settings = settings;
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Creates a PENDING_VERIFICATION account (plus its empty profile / employer record) and texts an OTP.
     *
     * @throws ApiException PHONE_PENDING_VERIFICATION, PHONE_ALREADY_REGISTERED or EMAIL_ALREADY_REGISTERED
     */
    public RegisterResponse register(RegisterRequest request, ClientInfo client) {
        String phone = PhoneNumbers.require(request.phone());
        String fullName = request.fullName().trim();
        Role role = request.role().toRole();
        String email = resolveEmail(role, request.email(), phone);

        users.findByPhone(phone).ifPresent(existing -> {
            throw existing.isPendingVerification()
                    ? new ApiException(ErrorCode.PHONE_PENDING_VERIFICATION)
                            .field("phone", ErrorCode.PHONE_PENDING_VERIFICATION.defaultMessage())
                            .detail("phone", phone)
                    : new ApiException(ErrorCode.PHONE_ALREADY_REGISTERED)
                            .field("phone", ErrorCode.PHONE_ALREADY_REGISTERED.defaultMessage());
        });
        if (users.existsByNormalizedEmail(email)) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_REGISTERED)
                    .field("email", ErrorCode.EMAIL_ALREADY_REGISTERED.defaultMessage());
        }

        AppUser user = users.save(AppUser.register(fullName, email, phone,
                passwordEncoder.encode(request.password()), role, OffsetDateTime.now(clock)));
        switch (role) {
            case JOB_SEEKER -> profiles.save(JobSeekerProfile.emptyFor(user.getId()));
            case EMPLOYER -> employers.save(Employer.register(user.getId(), fullName, email));
            default -> throw new IllegalStateException("Role cannot self-register: " + role);
        }
        audit.record(AuditAction.USER_REGISTERED, AuditResult.SUCCESS, user, client, Map.of("role", role.name()));

        OtpDispatch dispatch = otps.issue(user, OtpPurpose.PHONE_VERIFICATION, client);
        return new RegisterResponse(user.getId(), phone, role, dispatch.expiresInSeconds(),
                dispatch.resendAvailableInSeconds());
    }

    /**
     * Employers must supply a real email. Job seekers register and log in with phone number
     * alone (AUTH-07 UI), so they get a synthetic, internal-only address derived from their
     * phone number - unique and NOT NULL like every other {@code app_user.email} row, but never
     * shown to them and never usable as a login identifier in practice.
     */
    private String resolveEmail(Role role, String rawEmail, String normalizedPhone) {
        if (role != Role.EMPLOYER) {
            return "jobseeker+" + normalizedPhone.substring(1) + "@users.halokazi.local";
        }
        String email = Emails.normalize(rawEmail);
        if (email == null || email.isBlank()) {
            throw new ApiException(ErrorCode.VALIDATION_FAILED)
                    .field("email", "Email is required for employer accounts.");
        }
        return email;
    }

    /**
     * Checks the OTP, activates the account and signs the user in.
     *
     * @throws ApiException OTP_INVALID, OTP_EXPIRED or OTP_ATTEMPTS_EXCEEDED
     */
    public AuthResponse verifyPhone(VerifyOtpRequest request, ClientInfo client) {
        AppUser user = users.findByPhone(PhoneNumbers.require(request.phone()))
                .filter(AppUser::isPendingVerification)
                .orElseThrow(() -> new ApiException(ErrorCode.OTP_INVALID));

        otps.verify(user, OtpPurpose.PHONE_VERIFICATION, request.code(), client);
        user.markPhoneVerified(OffsetDateTime.now(clock));
        audit.success(AuditAction.PHONE_VERIFIED, user, client);
        return auth.signIn(user, client);
    }

    /**
     * Resends the verification code. Unknown and already-verified numbers get the same answer,
     * so the endpoint cannot be used to discover accounts.
     *
     * @throws ApiException OTP_RESEND_TOO_SOON or OTP_RESEND_LIMIT_REACHED
     */
    public ResendOtpResponse resendVerificationCode(ResendOtpRequest request, ClientInfo client) {
        OtpDispatch dispatch = users.findByPhone(PhoneNumbers.require(request.phone()))
                .filter(AppUser::isPendingVerification)
                .map(user -> otps.resend(user, OtpPurpose.PHONE_VERIFICATION, client))
                .orElseGet(() -> new OtpDispatch(settings.getInt(Setting.OTP_TTL_SECONDS),
                        properties.otp().resendCooldown().toSeconds()));
        return new ResendOtpResponse(ResendOtpResponse.MESSAGE, dispatch.expiresInSeconds(),
                dispatch.resendAvailableInSeconds());
    }
}
