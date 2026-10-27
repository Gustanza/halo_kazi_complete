package tz.co.halotel.halokazi.settings;

/**
 * SYSTEM_SETTING keys used by the application, with the fallback used if a row is missing
 * or unparsable (the seed values from 03_seed_reference.sql).
 */
public enum Setting {

    OTP_TTL_SECONDS(120),
    OTP_MAX_RESENDS(3),
    OTP_RESEND_WINDOW_MINUTES(30),
    SESSION_IDLE_TIMEOUT_MINUTES(10),
    PASSWORD_MIN_LENGTH(8),
    LOGIN_MAX_FAILED_ATTEMPTS(5),
    LOGIN_LOCK_MINUTES(15);

    private final int defaultValue;

    Setting(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int defaultValue() {
        return defaultValue;
    }
}
