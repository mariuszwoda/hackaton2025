package com.example.demogitlab.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // We're using AntPathMatcher via application.properties
    // This method is no longer needed
    /*
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setPatternParser(new PathPatternParser());
    }
    */

    /**
     * Customizes the Tomcat connector to allow encoded slashes in URLs.
     * This is necessary for handling project paths with slashes.
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers((Connector connector) -> {
                connector.setProperty("relaxedPathChars", "[]|{}^\\`");
                connector.setProperty("relaxedQueryChars", "[]|{}^\\`");
                connector.setProperty("allowEncodedSlash", "true");
            });
        };
    }
}
