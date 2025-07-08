package com.example.demogitlab.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gitLabReleaseApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GitLab Release Data API")
                        .description("API for retrieving release data from GitLab projects")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Hackaton 2025 Team")
                                .url("https://github.com/hackaton2025")
                                .email("info@hackaton2025.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Default Server URL")
                ));
    }
}