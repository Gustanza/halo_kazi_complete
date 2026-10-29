package tz.co.halotel.halokazi.common.error;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import tools.jackson.databind.DatabindException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiError> handleApi(ApiException ex) {
        ResponseEntity.BodyBuilder response = ResponseEntity.status(ex.code().status());
        if (ex.retryAfterSeconds() != null) {
            response.header(HttpHeaders.RETRY_AFTER, String.valueOf(ex.retryAfterSeconds()));
        }
        return response.body(ApiError.of(ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(ApiError.validation(fieldErrors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        // Point at the offending field (e.g. an unknown role value) when Jackson can tell us which.
        if (ex.getCause() instanceof DatabindException databind && !databind.getPath().isEmpty()) {
            String field = databind.getPath().getLast().getPropertyName();
            if (field != null) {
                return ResponseEntity.badRequest()
                        .body(ApiError.validation(List.of(new ApiError.FieldError(field, "Invalid value."))));
            }
        }
        return ResponseEntity.badRequest().body(ApiError.of(ErrorCode.MALFORMED_REQUEST));
    }

    /** A query or path parameter of the wrong type, e.g. ?page=abc or /jobs/abc. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(ApiError.validation(List.of(new ApiError.FieldError(ex.getName(), "Invalid value."))));
    }

    /** Safety net for races the service-level duplicate checks cannot see (unique indexes). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> handleIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(ErrorCode.CONFLICT.status()).body(ApiError.of(ErrorCode.CONFLICT));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiError> handleNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(ErrorCode.NOT_FOUND.status()).body(ApiError.of(ErrorCode.NOT_FOUND));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiError> handleMethod(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.status())
                .body(ApiError.of(ErrorCode.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ApiError> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(ErrorCode.UNSUPPORTED_MEDIA_TYPE.status())
                .body(ApiError.of(ErrorCode.UNSUPPORTED_MEDIA_TYPE));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        log.error("Unhandled error", ex);
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.status()).body(ApiError.of(ErrorCode.INTERNAL_ERROR));
    }
}
