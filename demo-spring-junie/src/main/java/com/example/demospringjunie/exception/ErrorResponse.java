package com.example.demospringjunie.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standard error response format for API errors.
 * Based on the ProblemDetails format (RFC 9457).
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    String type,
    String title,
    int status,
    String detail,
    String instance,
    LocalDateTime timestamp,
    Map<String, Object> extensions,
    List<ValidationError> validationErrors
) {
    /**
     * Represents a validation error for a specific field.
     */
    public record ValidationError(
        String field,
        String message
    ) {
    }
}