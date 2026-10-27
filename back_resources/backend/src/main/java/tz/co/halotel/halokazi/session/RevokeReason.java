package tz.co.halotel.halokazi.session;

/** Values allowed by ck_user_session_reason. */
public enum RevokeReason {
    LOGOUT,
    IDLE_TIMEOUT,
    PASSWORD_CHANGED,
    SUSPENDED,
    ADMIN_REVOKED
}
