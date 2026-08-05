package com.cryptalk.common;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<Map<String, Object>> handleApi(ApiException exception) {
        return ResponseEntity.status(exception.status()).body(error(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError == null ? "요청값을 확인해 주세요." : fieldError.getField() + ": " + fieldError.getDefaultMessage();
        return ResponseEntity.badRequest().body(error(message));
    }

    private Map<String, Object> error(String message) {
        return Map.of("message", message, "timestamp", Instant.now().toString());
    }
}
