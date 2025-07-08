package com.example.demospringjunie.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Typed configuration properties for the application.
 * Binds properties with the "app.junie" prefix from application.properties.
 */
@ConfigurationProperties(prefix = "app.junie")
@Validated
@Getter
@Setter
public class AppProperties {

    /**
     * Feature flag to enable/disable certain functionality.
     */
    @NotNull
    private Boolean featureEnabled;

    /**
     * Maximum number of items to return per page in paginated responses.
     */
    @Min(1)
    @Max(100)
    private Integer maxItemsPerPage = 20;
}
