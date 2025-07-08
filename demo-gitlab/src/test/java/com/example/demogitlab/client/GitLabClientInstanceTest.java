package com.example.demogitlab.client;

import com.example.demogitlab.config.GitLabProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class GitLabClientInstanceTest {

    @Autowired
    private GitLabClient gitLabClient;

    @Autowired
    private GitLabProperties gitLabProperties;

    @Autowired
    @Qualifier("gitLabWebClients")
    private Map<String, WebClient> gitLabWebClients;

    @Test
    void shouldHaveDefaultInstance() {
        // Given
        String defaultInstance = gitLabProperties.getDefaultInstance();

        // When & Then
        assertNotNull(defaultInstance);
        assertEquals("mock", defaultInstance);
    }

    @Test
    void shouldHaveMultipleInstances() {
        // Debug information
        System.out.println("[DEBUG_LOG] Default instance: " + gitLabProperties.getDefaultInstance());
        System.out.println("[DEBUG_LOG] Instances: " + gitLabProperties.getInstances().keySet());
        System.out.println("[DEBUG_LOG] WebClients: " + gitLabWebClients.keySet());
        System.out.println("[DEBUG_LOG] WebClients size: " + gitLabWebClients.size());

        // When & Then
        assertNotNull(gitLabWebClients);
        assertEquals(4, gitLabWebClients.size());
        assertTrue(gitLabWebClients.containsKey("mock"));
        assertTrue(gitLabWebClients.containsKey("mock-company"));
        assertTrue(gitLabWebClients.containsKey("public"));
        assertTrue(gitLabWebClients.containsKey("company"));
    }

    @Test
    void shouldGetDifferentWebClientsForDifferentInstances() {
        // Debug information
        System.out.println("[DEBUG_LOG] Default instance: " + gitLabProperties.getDefaultInstance());
        System.out.println("[DEBUG_LOG] WebClients map: " + gitLabWebClients);

        // When
        WebClient defaultWebClient = gitLabClient.getDefaultWebClient();
        WebClient mockCompanyWebClient = gitLabClient.getWebClient("mock-company");

        // Debug more information
        System.out.println("[DEBUG_LOG] Default WebClient: " + defaultWebClient);
        System.out.println("[DEBUG_LOG] Mock Company WebClient: " + mockCompanyWebClient);
        System.out.println("[DEBUG_LOG] Are they the same object? " + (defaultWebClient == mockCompanyWebClient));
        System.out.println("[DEBUG_LOG] WebClient from map (mock): " + gitLabWebClients.get("mock"));
        System.out.println("[DEBUG_LOG] WebClient from map (mock-company): " + gitLabWebClients.get("mock-company"));

        // Then
        assertNotNull(defaultWebClient);
        assertNotNull(mockCompanyWebClient);
        assertNotSame(defaultWebClient, mockCompanyWebClient);
    }
}
