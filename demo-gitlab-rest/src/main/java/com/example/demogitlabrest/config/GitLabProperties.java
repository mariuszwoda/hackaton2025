package com.example.demogitlabrest.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "gitlab.api")
public class GitLabProperties {

    /**
     * Base URL for the GitLab API (legacy support)
     */
    @NotBlank
    private String baseUrl;

    /**
     * Timeout in milliseconds for API requests (legacy support)
     */
    @Positive
    private int timeout;

    /**
     * Default GitLab instance to use
     */
    private String defaultInstance = "public";

    /**
     * Map of GitLab instances
     */
    @Valid
    private Map<String, GitLabInstanceProperties> instances = new HashMap<>();

    /**
     * Get the default GitLab instance properties
     * 
     * @return the default GitLab instance properties
     */
    public GitLabInstanceProperties getDefaultInstanceProperties() {
        // If instances map is empty or doesn't contain the default instance,
        // create a default instance using the legacy properties
        if (instances.isEmpty() || !instances.containsKey(defaultInstance)) {
            GitLabInstanceProperties defaultProps = new GitLabInstanceProperties();
            defaultProps.setBaseUrl(baseUrl);
            defaultProps.setTimeout(timeout);
            return defaultProps;
        }

        return instances.get(defaultInstance);
    }

    /**
     * Properties for a specific GitLab instance
     */
    @Getter
    @Setter
    @Validated
    public static class GitLabInstanceProperties {
        /**
         * Base URL for the GitLab API
         */
        @NotBlank
        private String baseUrl;

        /**
         * Timeout in milliseconds for API requests
         */
        @Positive
        private int timeout = 30000;

        /**
         * Authentication token for the GitLab API
         */
        private String token;

        /**
         * Whether to use token authentication
         */
        private boolean useTokenAuth = false;
    }
}