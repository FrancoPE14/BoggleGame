package com.anteaters.boggle.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Centralized exception handling for REST APIs.
 *
 * This advice converts common validation and state transition failures into
 * stable JSON responses that frontend clients can handle consistently.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles invalid client input such as malformed parameters or references
     * to missing resources.
     *
     * @param ex exception thrown by controllers/services for invalid input
     * @return JSON error response with HTTP 400
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

    /**
     * Handles invalid state transitions such as attempting to join a session
     * that is already started, already full, or already contains the player.
     *
     * HTTP 409 is used because the request is syntactically valid but conflicts
     * with the current server-side state.
     *
     * @param ex exception thrown by controllers/services for invalid state transitions
     * @return JSON error response with HTTP 409
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleIllegalState(IllegalStateException ex) {
        return Map.of(
                "status", 409,
                "error", "Conflict",
                "message", ex.getMessage()
        );
    }
}