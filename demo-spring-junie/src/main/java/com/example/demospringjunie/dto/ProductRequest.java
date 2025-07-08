package com.example.demospringjunie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a product.
 */
public record ProductRequest(
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    String name,

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    @Positive(message = "Price must be positive")
    BigDecimal price,

    Boolean active
) {
}