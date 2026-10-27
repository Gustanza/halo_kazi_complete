package tz.co.halotel.halokazi.audit;

/** Stored in LOGIN_ATTEMPT.FAILURE_REASON; never returned to the client. */
public enum LoginFailureReason {
    UNKNOWN_IDENTIFIER,
    BAD_PASSWORD,
    ACCOUNT_LOCKED,
    PHONE_NOT_VERIFIED,
    ACCOUNT_DISABLED
}
