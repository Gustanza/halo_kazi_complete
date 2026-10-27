package tz.co.halotel.halokazi.otp;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tz.co.halotel.halokazi.audit.AuditAction;
import tz.co.halotel.halokazi.audit.AuditResult;
import tz.co.halotel.halokazi.audit.AuditService;
import tz.co.halotel.halokazi.common.config.HalokaziProperties;
import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.BusinessTransaction;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.common.web.ClientInfo;
import tz.co.halotel.halokazi.settings.Setting;
import tz.co.halotel.halokazi.settings.SettingsService;
import tz.co.halotel.halokazi.sms.SmsService;
import tz.co.halotel.halokazi.user.AppUser;

/** Issues, resends and verifies SMS one-time codes (AUTH-02, AUTH-03). */
@Service
@BusinessTransaction
public class OtpService {

    private static final int CODE_LENGTH = 6;
    /** ck_otp_code_resend caps resend_count at 3 whatever OTP_MAX_RESENDS says. */
    private static final int SCHEMA_MAX_RESENDS = 3;

    private final SecureRandom random = new SecureRandom();
    private final OtpCodeRepository repository;
    private final PasswordEncoder encoder;
    private final SmsService sms;
    private final AuditService audit;
    private final SettingsService settings;
    private final HalokaziProperties.Otp config;
    private final Clock clock;

    public OtpService(OtpCodeRepository repository, PasswordEncoder encoder, SmsService sms, AuditService audit,
            SettingsService settings, HalokaziProperties properties, Clock clock) {
        this.repository = repository;
        this.encoder = encoder;
        this.sms = sms;
        this.audit = audit;
        this.settings = settings;
        this.config = properties.otp();
        this.clock = clock;
    }

    /** Starts a new resend control window with a fresh code. */
    public OtpDispatch issue(AppUser user, OtpPurpose purpose, ClientInfo client) {
        Duration ttl = settings.getSeconds(Setting.OTP_TTL_SECONDS);
        String code = newCode();
        // Deliver before saving: a failed send must not start the resend cooldown.
        deliver(user, purpose, code, ttl);
        repository.save(OtpCode.issue(user.getId(), user.getPhone(), purpose, encoder.encode(code), now().plus(ttl),
                client.ip()));
        audit.record(AuditAction.OTP_SENT, AuditResult.SUCCESS, user, client, Map.of("purpose", purpose.name()));
        return new OtpDispatch(ttl.toSeconds(), config.resendCooldown().toSeconds());
    }

    /**
     * Sends a new code, honouring the cooldown and the OTP_MAX_RESENDS / OTP_RESEND_WINDOW_MINUTES limit.
     *
     * @throws ApiException OTP_RESEND_TOO_SOON or OTP_RESEND_LIMIT_REACHED, with retryAfterSeconds
     */
    public OtpDispatch resend(AppUser user, OtpPurpose purpose, ClientInfo client) {
        OffsetDateTime now = now();
        Duration ttl = settings.getSeconds(Setting.OTP_TTL_SECONDS);
        Duration window = settings.getMinutes(Setting.OTP_RESEND_WINDOW_MINUTES);
        int maxResends = Math.min(settings.getInt(Setting.OTP_MAX_RESENDS), SCHEMA_MAX_RESENDS);

        Optional<OtpCode> current = repository.findFirstByUserIdAndPurposeAndConsumedAtIsNullOrderByIdDesc(
                user.getId(), purpose);
        if (current.isEmpty() || !current.get().getCreatedAt().plus(window).isAfter(now)) {
            return issue(user, purpose, client);
        }
        OtpCode otp = current.get();

        OffsetDateTime nextAllowed = otp.lastSentAt(ttl).plus(config.resendCooldown());
        if (nextAllowed.isAfter(now)) {
            throw new ApiException(ErrorCode.OTP_RESEND_TOO_SOON).retryAfter(secondsBetween(now, nextAllowed));
        }
        OffsetDateTime windowEnd = otp.getCreatedAt().plus(window);
        if (otp.getResendCount() >= maxResends) {
            throw new ApiException(ErrorCode.OTP_RESEND_LIMIT_REACHED).retryAfter(secondsBetween(now, windowEnd));
        }

        String code = newCode();
        // Deliver before reissuing: a failed send must not use up one of the allowed resends.
        deliver(user, purpose, code, ttl);
        otp.reissue(encoder.encode(code), now.plus(ttl));
        audit.record(AuditAction.OTP_RESENT, AuditResult.SUCCESS, user, client,
                Map.of("purpose", purpose.name(), "resendCount", otp.getResendCount()));

        long nextResendIn = otp.getResendCount() >= maxResends
                ? secondsBetween(now, windowEnd)
                : config.resendCooldown().toSeconds();
        return new OtpDispatch(ttl.toSeconds(), nextResendIn);
    }

    /**
     * Consumes the current code if {@code code} matches.
     *
     * @throws ApiException OTP_INVALID (with remainingAttempts), OTP_EXPIRED or OTP_ATTEMPTS_EXCEEDED
     */
    public void verify(AppUser user, OtpPurpose purpose, String code, ClientInfo client) {
        OffsetDateTime now = now();
        int maxAttempts = config.maxVerifyAttempts();
        OtpCode otp = repository.findFirstByUserIdAndPurposeAndConsumedAtIsNullOrderByIdDesc(user.getId(), purpose)
                .orElseThrow(() -> new ApiException(ErrorCode.OTP_EXPIRED));

        if (otp.getAttemptCount() >= maxAttempts) {
            throw new ApiException(ErrorCode.OTP_ATTEMPTS_EXCEEDED);
        }
        if (otp.isExpired(now)) {
            throw new ApiException(ErrorCode.OTP_EXPIRED);
        }
        if (!encoder.matches(code, otp.getCodeHash())) {
            otp.recordFailedAttempt();
            int remaining = maxAttempts - otp.getAttemptCount();
            audit.record(AuditAction.OTP_VERIFY_FAILED, AuditResult.FAILURE, user, client,
                    Map.of("purpose", purpose.name(), "attempt", otp.getAttemptCount()));
            if (remaining <= 0) {
                throw new ApiException(ErrorCode.OTP_ATTEMPTS_EXCEEDED);
            }
            throw new ApiException(ErrorCode.OTP_INVALID).detail("remainingAttempts", remaining);
        }
        otp.consume(now);
    }

    private void deliver(AppUser user, OtpPurpose purpose, String code, Duration ttl) {
        String text = "Your Halokazi verification code is " + code + ". It expires in " + describe(ttl) + ".";
        sms.send(user.getId(), user.getPhone(), purpose.name(), text);
    }

    private String newCode() {
        int bound = (int) Math.pow(10, CODE_LENGTH);
        return String.format("%0" + CODE_LENGTH + "d", random.nextInt(bound));
    }

    private static String describe(Duration ttl) {
        long seconds = ttl.toSeconds();
        return seconds % 60 == 0 ? (seconds / 60) + " minute" + (seconds == 60 ? "" : "s") : seconds + " seconds";
    }

    private static long secondsBetween(OffsetDateTime from, OffsetDateTime to) {
        return Math.max(1, (long) Math.ceil(Duration.between(from, to).toMillis() / 1000.0));
    }

    private OffsetDateTime now() {
        return OffsetDateTime.now(clock);
    }
}
