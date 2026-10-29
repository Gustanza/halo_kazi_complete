package tz.co.halotel.halokazi.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a business rule is violated (e.g. max CV limit, duplicate title,
 * incomplete CV finalization, version conflict).
 * Carries an error code for programmatic handling and an HTTP status for the response.
 */
public class BusinessRuleException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public BusinessRuleException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /** 409 Conflict — resource state conflicts (duplicate title, max limit, version mismatch, FK reference) */
    public static BusinessRuleException conflict(String errorCode, String message) {
        return new BusinessRuleException(errorCode, message, HttpStatus.CONFLICT);
    }

    /** 422 Unprocessable Entity — business validation failures (incomplete CV, invalid state transition) */
    public static BusinessRuleException unprocessable(String errorCode, String message) {
        return new BusinessRuleException(errorCode, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
