package tz.co.halotel.halokazi.audit;

public enum AuditAction {
    USER_REGISTERED,
    OTP_SENT,
    OTP_RESENT,
    OTP_VERIFY_FAILED,
    PHONE_VERIFIED,
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    LOGOUT
}
