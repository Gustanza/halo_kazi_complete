package tz.co.halotel.halokazi.common.error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An expected business rejection, rendered by {@link GlobalExceptionHandler}. Services throw it;
 * controllers never build error responses themselves.
 */
public class ApiException extends RuntimeException {

    private final ErrorCode code;
    private Long retryAfterSeconds;
    private final List<ApiError.FieldError> fieldErrors = new ArrayList<>();
    private final Map<String, Object> details = new LinkedHashMap<>();

    public ApiException(ErrorCode code) {
        this(code, code.defaultMessage());
    }

    public ApiException(ErrorCode code, String message) {
        // Rejections are control flow, not bugs: skip the stack trace.
        super(message, null, false, false);
        this.code = code;
    }

    public ApiException retryAfter(long seconds) {
        this.retryAfterSeconds = Math.max(1, seconds);
        return this;
    }

    public ApiException field(String field, String message) {
        fieldErrors.add(new ApiError.FieldError(field, message));
        return this;
    }

    public ApiException detail(String key, Object value) {
        details.put(key, value);
        return this;
    }

    public ErrorCode code() {
        return code;
    }

    public Long retryAfterSeconds() {
        return retryAfterSeconds;
    }

    public List<ApiError.FieldError> fieldErrors() {
        return Collections.unmodifiableList(fieldErrors);
    }

    public Map<String, Object> details() {
        return Collections.unmodifiableMap(details);
    }
}
