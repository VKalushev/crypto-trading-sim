package com.crypto_trading_sim.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<Object> handleAlreadyExists(AlreadyExistException ex, WebRequest request) {
        logger.warn("AlreadyExistException occurred: {}", ex.getMessage());
        Map<String, Object> body = createErrorResponse(ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest request) {
        logger.warn("NotFoundException occurred: {}", ex.getMessage());
        Map<String, Object> body = createErrorResponse(ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        logger.warn("BadCredentialsException occurred: {}", ex.getMessage());
        Map<String, Object> body = createErrorResponse("Invalid username or password");
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        logger.warn("AccessDeniedException occurred: {}", ex.getMessage());
        Map<String, Object> body = createErrorResponse("Access denied");
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("Validation error occurred: {}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        Map<String, Object> body = createErrorResponse("Validation failed: " + errorMessage);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred", ex);

        Map<String, Object> body = createErrorResponse("Unexpected error occurred");
        ex.printStackTrace();

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({InvalidTransactionException.class, InsufficientFundsException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleInvalidTransaction(RuntimeException ex, WebRequest request) {
        logger.warn(String.format("%s occurred: {}", ex.getClass().getSimpleName()), ex.getMessage());
        Map<String, Object> body = createErrorResponse(ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        return body;
    }
}