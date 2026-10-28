package tz.co.halotel.halokazi.common.error;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/** The single error body returned by every endpoint. Empty/absent parts are omitted. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
        String code,
        String message,
        List<FieldError> fieldErrors,
        Long retryAfterSeconds,
        Map<String, Object> details) {

    public record FieldError(String field, String message) {
    }

    public static ApiError of(ErrorCode code) {
        return new ApiError(code.name(), code.defaultMessage(), List.of(), null, Map.of());
    }

    public static ApiError of(ApiException ex) {
        return new ApiError(ex.code().name(), ex.getMessage(), ex.fieldErrors(), ex.retryAfterSeconds(), ex.details());
    }

    public static ApiError validation(List<FieldError> fieldErrors) {
        ErrorCode code = ErrorCode.VALIDATION_FAILED;
        return new ApiError(code.name(), code.defaultMessage(), fieldErrors, null, Map.of());
    }
}
