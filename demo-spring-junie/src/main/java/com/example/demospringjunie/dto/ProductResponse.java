package com.example.demospringjunie.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for returning product data to clients.
 */
public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean active
) {
}