package com.example.demogitlabrest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(GitLabProperties.class)
public class GitLabConfig {

    /**
     * Legacy bean for backward compatibility
     */
    @Bean
    public RestTemplate gitLabRestTemplate(GitLabProperties gitLabProperties, ObjectMapper objectMapper) {
        // Use the default instance properties
        GitLabProperties.GitLabInstanceProperties instanceProps = gitLabProperties.getDefaultInstanceProperties();
        return buildRestTemplate(instanceProps);
    }

    /**
     * Bean providing a map of RestTemplate instances for different GitLab servers
     */
    @Bean
    public Map<String, RestTemplate> gitLabRestTemplates(GitLabProperties gitLabProperties, ObjectMapper objectMapper) {
        Map<String, RestTemplate> clients = new HashMap<>();

        log.info("Creating RestTemplate instances for GitLab servers");
        log.info("Default instance: {}", gitLabProperties.getDefaultInstance());
        log.info("Configured instances: {}", gitLabProperties.getInstances().keySet());

        // Add the default instance
        GitLabProperties.GitLabInstanceProperties defaultProps = gitLabProperties.getDefaultInstanceProperties();
        clients.put(gitLabProperties.getDefaultInstance(), buildRestTemplate(defaultProps));
        log.info("Added default instance: {}", gitLabProperties.getDefaultInstance());

        // Add all configured instances
        gitLabProperties.getInstances().forEach((name, props) -> {
            if (!name.equals(gitLabProperties.getDefaultInstance())) {
                clients.put(name, buildRestTemplate(props));
                log.info("Added instance: {}", name);
            }
        });

        log.info("Created {} RestTemplate instances: {}", clients.size(), clients.keySet());

        return clients;
    }

    /**
     * Build a RestTemplate for a specific GitLab instance
     */
    private RestTemplate buildRestTemplate(GitLabProperties.GitLabInstanceProperties instanceProps) {
        RestTemplateBuilder builder = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(instanceProps.getTimeout()))
                .setReadTimeout(Duration.ofMillis(instanceProps.getTimeout()));

        // Add token authentication if configured
        if (instanceProps.isUseTokenAuth() && instanceProps.getToken() != null && !instanceProps.getToken().isEmpty()) {
            builder = builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + instanceProps.getToken());
        }

        // Add logging interceptor
        builder = builder.additionalInterceptors(loggingInterceptor());

        return builder.build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.debug("Request: {} {}", request.getMethod(), request.getURI());
            return execution.execute(request, body);
        };
    }
}