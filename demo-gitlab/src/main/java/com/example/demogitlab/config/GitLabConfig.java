package com.example.demogitlab.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(GitLabProperties.class)
public class GitLabConfig {

    /**
     * Legacy bean for backward compatibility
     */
    @Bean
    public WebClient gitLabWebClient(GitLabProperties gitLabProperties, ObjectMapper objectMapper) {
        // Use the default instance properties
        GitLabProperties.GitLabInstanceProperties instanceProps = gitLabProperties.getDefaultInstanceProperties();
        return buildWebClient(instanceProps, objectMapper);
    }

    /**
     * Bean providing a map of WebClient instances for different GitLab servers
     */
    @Bean
    public Map<String, WebClient> gitLabWebClients(GitLabProperties gitLabProperties, ObjectMapper objectMapper) {
        Map<String, WebClient> clients = new HashMap<>();

        log.info("Creating WebClient instances for GitLab servers");
        log.info("Default instance: {}", gitLabProperties.getDefaultInstance());
        log.info("Configured instances: {}", gitLabProperties.getInstances().keySet());

        // Add the default instance
        GitLabProperties.GitLabInstanceProperties defaultProps = gitLabProperties.getDefaultInstanceProperties();
        clients.put(gitLabProperties.getDefaultInstance(), buildWebClient(defaultProps, objectMapper));
        log.info("Added default instance: {}", gitLabProperties.getDefaultInstance());

        // Add all configured instances
        gitLabProperties.getInstances().forEach((name, props) -> {
            if (!name.equals(gitLabProperties.getDefaultInstance())) {
                clients.put(name, buildWebClient(props, objectMapper));
                log.info("Added instance: {}", name);
            }
        });

        log.info("Created {} WebClient instances: {}", clients.size(), clients.keySet());

        return clients;
    }

    /**
     * Build a WebClient for a specific GitLab instance
     */
    private WebClient buildWebClient(GitLabProperties.GitLabInstanceProperties instanceProps, ObjectMapper objectMapper) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, instanceProps.getTimeout())
                .responseTimeout(Duration.ofMillis(instanceProps.getTimeout()))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(instanceProps.getTimeout(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(instanceProps.getTimeout(), TimeUnit.MILLISECONDS)));

        // Configure exchange strategies with our custom ObjectMapper
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                })
                .build();

        WebClient.Builder builder = WebClient.builder()
                .baseUrl(instanceProps.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .filter(logRequest())
                .filter(logResponse());

        // Add token authentication if configured
        if (instanceProps.isUseTokenAuth() && instanceProps.getToken() != null && !instanceProps.getToken().isEmpty()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + instanceProps.getToken());
        }

        return builder.build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.debug("Response status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}
