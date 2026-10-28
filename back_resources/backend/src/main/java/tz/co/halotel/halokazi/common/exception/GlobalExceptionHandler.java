package tz.co.halotel.halokazi.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tz.co.halotel.halokazi.common.api.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        // Unpack Oracle application errors if present
        String msg = ex.getMessage();
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause.getMessage() != null && cause.getMessage().contains("ORA-200")) {
                msg = extractOracleMessage(cause.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(msg));
            }
            cause = cause.getCause();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(msg != null ? msg : "An unexpected server error occurred"));
    }

    private String extractOracleMessage(String raw) {
        // Extracts "ORA-200XX: Friendly text" before newline
        int idx = raw.indexOf("ORA-200");
        if (idx >= 0) {
            int end = raw.indexOf("\n", idx);
            if (end > idx) {
                return raw.substring(idx, end).replaceAll("^ORA-\\d+:\\s*", "");
            }
            return raw.substring(idx).replaceAll("^ORA-\\d+:\\s*", "");
        }
        return raw;
    }
}
