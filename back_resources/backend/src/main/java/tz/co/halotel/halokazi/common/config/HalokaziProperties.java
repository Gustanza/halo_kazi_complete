package tz.co.halotel.halokazi.common.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Technical configuration bound from {@code halokazi.*}. Business rules that admins may tune
 * (OTP TTL, lockout, idle timeout, ...) are read from SYSTEM_SETTING instead.
 */
@ConfigurationProperties(prefix = "halokazi")
public record HalokaziProperties(Cors cors, Session session, Otp otp, Settings settings) {

    public record Cors(List<String> allowedOrigins) {
    }

    /**
     * @param absoluteTtl           hard cap on a session's lifetime, regardless of activity
     * @param activityWriteInterval minimum gap between last_activity_at writes, to avoid a DB write per request
     */
    public record Session(Duration absoluteTtl, Duration activityWriteInterval) {
    }

    /**
     * @param resendCooldown    minimum gap between two OTP sends to the same user
     * @param maxVerifyAttempts wrong guesses allowed per code before it must be resent
     */
    public record Otp(Duration resendCooldown, int maxVerifyAttempts) {
    }

    public record Settings(Duration cacheTtl) {
    }
}
