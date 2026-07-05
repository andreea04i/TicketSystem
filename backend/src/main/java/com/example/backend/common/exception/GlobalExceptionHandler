package com.example.backend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication
        .BadCredentialsException;
import org.springframework.web.bind
        .MethodArgumentNotValidException;
import org.springframework.web.bind.annotation
        .ExceptionHandler;
import org.springframework.web.bind.annotation
        .RestControllerAdvice;
import org.springframework.web.server
        .ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>>
    handleBadCredentials(
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Email sau parolă incorectă",
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors =
                new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.putIfAbsent(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Datele trimise nu sunt valide",
                request.getRequestURI(),
                validationErrors
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>>
    handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        int statusCode =
                exception.getStatusCode().value();

        HttpStatus status =
                HttpStatus.resolve(statusCode);

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String message =
                exception.getReason() != null
                        ? exception.getReason()
                        : status.getReasonPhrase();

        return buildResponse(
                status,
                message,
                request.getRequestURI(),
                null
        );
    }

    private ResponseEntity<Map<String, Object>>
    buildResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> validationErrors
    ) {
        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);

        if (validationErrors != null) {
            body.put(
                    "validationErrors",
                    validationErrors
            );
        }

        return ResponseEntity
                .status(status)
                .body(body);
    }
}