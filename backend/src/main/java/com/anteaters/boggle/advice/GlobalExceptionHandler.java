package com.anteaters.boggle.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Centralized exception handling for REST APIs.
 *
 * Converts common input validation failures into HTTP 400 responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles invalid client input (ex: blank word, non-alphabetic input, etc.).
     *
     * @param ex exception thrown by controllers/services for invalid input
     * @return JSON error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage()
        );
    }
}