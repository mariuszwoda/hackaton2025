package com.example.demospringjunie.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration class.
 * Enables the typed configuration properties.
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
class AppConfig {
    // Additional beans and configuration can be added here
}