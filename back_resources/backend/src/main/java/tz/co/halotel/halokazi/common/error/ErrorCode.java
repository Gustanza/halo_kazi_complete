package tz.co.halotel.halokazi.common.error;

import org.springframework.http.HttpStatus;

/** Stable, client-facing error codes. The UI switches on the code, never on the message. */
public enum ErrorCode {

    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "One or more fields are invalid."),
    MALFORMED_REQUEST(HttpStatus.BAD_REQUEST, "The request body could not be read."),

    PHONE_ALREADY_REGISTERED(HttpStatus.CONFLICT, "This phone number is already registered."),
    EMAIL_ALREADY_REGISTERED(HttpStatus.CONFLICT, "This email address is already registered."),
    PHONE_PENDING_VERIFICATION(HttpStatus.CONFLICT,
            "This phone number is registered but not yet verified. Verify it or request a new code."),
    CONFLICT(HttpStatus.CONFLICT, "The request conflicts with existing data."),

    OTP_INVALID(HttpStatus.BAD_REQUEST, "The verification code is incorrect."),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST, "The verification code has expired. Request a new code."),
    OTP_ATTEMPTS_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Too many incorrect attempts. Request a new code."),
    OTP_RESEND_TOO_SOON(HttpStatus.TOO_MANY_REQUESTS, "Please wait before requesting another code."),
    OTP_RESEND_LIMIT_REACHED(HttpStatus.TOO_MANY_REQUESTS, "You have requested too many codes. Try again later."),
    SMS_DELIVERY_FAILED(HttpStatus.BAD_GATEWAY, "We could not send the SMS. Please request a new code."),

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Incorrect phone/email or password."),
    ACCOUNT_LOCKED(HttpStatus.LOCKED, "Too many failed login attempts. Your account is temporarily locked."),
    PHONE_NOT_VERIFIED(HttpStatus.FORBIDDEN, "Verify your phone number before logging in."),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "This account is not active. Contact support."),
    JOB_SEEKERS_ONLY(HttpStatus.FORBIDDEN, "This is only available to job seeker accounts."),
    CURRENT_PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "Your current password is incorrect."),

    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "You need to log in."),
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "Your session has expired. Please log in again."),

    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found."),
    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "This job does not exist or is no longer open."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not supported for this endpoint."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Send the request body as JSON (Content-Type: application/json)."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again.");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus status() {
        return status;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
