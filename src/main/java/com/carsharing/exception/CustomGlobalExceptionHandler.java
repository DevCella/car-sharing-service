package com.carsharing.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST);
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(this::getErrorMessage)
                .toList();
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex) {
        Map<String, Object> body = createErrorBody(HttpStatus.NOT_FOUND);
        body.put("error", "Entity Not Found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({RegistrationException.class, PaymentProcessException.class})
    public ResponseEntity<Object> handleConflictExceptions(RuntimeException ex) {
        Map<String, Object> body = createErrorBody(HttpStatus.CONFLICT);
        body.put("error", ex.getClass().getSimpleName());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StripeSessionException.class)
    public ResponseEntity<Object> handleStripeSessionException(
            StripeSessionException ex) {
        Map<String, Object> body = createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR);
        body.put("error", "Stripe Session Error");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = createErrorBody(HttpStatus.FORBIDDEN);
        body.put("error", "Access Denied");
        body.put("message", "You do not have permission to perform this action");
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    private Map<String, Object> createErrorBody(HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        return body;
    }

    private String getErrorMessage(ObjectError objErr) {
        if (objErr instanceof FieldError fieldError) {
            String field = fieldError.getField();
            return field + " " + objErr.getDefaultMessage();
        }
        return objErr.getDefaultMessage();
    }
}
