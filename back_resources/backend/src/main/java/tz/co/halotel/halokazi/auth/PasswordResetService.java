package tz.co.halotel.halokazi.auth;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tz.co.halotel.halokazi.audit.AuditAction;
import tz.co.halotel.halokazi.audit.AuditResult;
import tz.co.halotel.halokazi.audit.AuditService;
import tz.co.halotel.halokazi.auth.dto.ForgotPasswordRequest;
import tz.co.halotel.halokazi.auth.dto.ResendOtpResponse;
import tz.co.halotel.halokazi.auth.dto.ResetPasswordRequest;
import tz.co.halotel.halokazi.common.config.HalokaziProperties;
import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.BusinessTransaction;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.common.web.ClientInfo;
import tz.co.halotel.halokazi.otp.OtpDispatch;
import tz.co.halotel.halokazi.otp.OtpPurpose;
import tz.co.halotel.halokazi.otp.OtpService;
import tz.co.halotel.halokazi.session.RevokeReason;
import tz.co.halotel.halokazi.session.SessionService;
import tz.co.halotel.halokazi.settings.Setting;
import tz.co.halotel.halokazi.settings.SettingsService;
import tz.co.halotel.halokazi.user.AppUser;
import tz.co.halotel.halokazi.user.AppUserRepository;

/**
 * Forgotten password (AUTH-05) by SMS code: the code goes to the account's verified phone, using the
 * PASSWORD_RESET purpose of otp_code and the same TTL / resend limits as phone verification.
 */
@Service
@BusinessTransaction
public class PasswordResetService {

    static final String SENT_MESSAGE =
            "If an active account matches, a reset code has been sent by SMS to its phone number.";

    private final AppUserRepository users;
    private final OtpService otps;
    private final SessionService sessions;
    private final PasswordEncoder encoder;
    private final AuditService audit;
    private final SettingsService settings;
    private final HalokaziProperties properties;
    private final Clock clock;

    public PasswordResetService(AppUserRepository users, OtpService otps, SessionService sessions,
            PasswordEncoder encoder, AuditService audit, SettingsService settings, HalokaziProperties properties,
            Clock clock) {
        this.users = users;
        this.otps = otps;
        this.sessions = sessions;
        this.encoder = encoder;
        this.audit = audit;
        this.settings = settings;
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Sends (or re-sends) a reset code. Unknown, unverified and disabled accounts get the same answer, so
     * the endpoint cannot be used to discover accounts.
     *
     * @throws ApiException OTP_RESEND_TOO_SOON or OTP_RESEND_LIMIT_REACHED (rate limits, AUTH-13)
     */
    public ResendOtpResponse requestReset(ForgotPasswordRequest request, ClientInfo client) {
        OtpDispatch dispatch = users.findByLoginIdentifier(request.identifier())
                .filter(AppUser::isActive)
                .map(user -> {
                    OtpDispatch sent = otps.resend(user, OtpPurpose.PASSWORD_RESET, client);
                    audit.success(AuditAction.PASSWORD_RESET_REQUESTED, user, client);
                    return sent;
                })
                .orElseGet(() -> new OtpDispatch(settings.getInt(Setting.OTP_TTL_SECONDS),
                        properties.otp().resendCooldown().toSeconds()));
        return new ResendOtpResponse(SENT_MESSAGE, dispatch.expiresInSeconds(), dispatch.resendAvailableInSeconds());
    }

    /**
     * Checks the code and sets the new password. Every session of the account is ended, and a login
     * lock is lifted.
     *
     * @throws ApiException VALIDATION_FAILED, OTP_INVALID, OTP_EXPIRED or OTP_ATTEMPTS_EXCEEDED
     */
    public void resetPassword(ResetPasswordRequest request, ClientInfo client) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new ApiException(ErrorCode.VALIDATION_FAILED).field("confirmPassword", "Passwords do not match.");
        }
        AppUser user = users.findByLoginIdentifier(request.identifier())
                .filter(AppUser::isActive)
                .orElseThrow(() -> new ApiException(ErrorCode.OTP_INVALID));

        otps.verify(user, OtpPurpose.PASSWORD_RESET, request.code(), client);
        user.resetPassword(encoder.encode(request.newPassword()), OffsetDateTime.now(clock));
        int signedOut = sessions.revokeAll(user.getId(), RevokeReason.PASSWORD_CHANGED);
        audit.record(AuditAction.PASSWORD_RESET, AuditResult.SUCCESS, user, client,
                Map.of("sessionsEnded", signedOut));
    }
}
