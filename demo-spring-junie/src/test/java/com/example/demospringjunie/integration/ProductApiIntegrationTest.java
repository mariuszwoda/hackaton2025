package com.example.demospringjunie.integration;

import com.example.demospringjunie.dto.ProductRequest;
import com.example.demospringjunie.dto.ProductResponse;
import com.example.demospringjunie.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Product API.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/products";
        productRepository.deleteAll();
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws JsonProcessingException {
        // Given
        ProductRequest request = new ProductRequest("Integration Test Product", "Description for integration test", new BigDecimal("29.99"), true);

        // When
        ResponseEntity<ProductResponse> response = restTemplate.postForEntity(
                baseUrl,
                request,
                ProductResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Integration Test Product");
        assertThat(response.getBody().description()).isEqualTo("Description for integration test");
        assertThat(response.getBody().price()).isEqualTo(new BigDecimal("29.99"));
        assertThat(response.getBody().active()).isTrue();
        assertThat(response.getBody().id()).isNotNull();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Given
        ProductRequest request = new ProductRequest("Product to Retrieve", "Description for retrieval test", new BigDecimal("39.99"), true);
        ProductResponse createdProduct = restTemplate.postForEntity(
                baseUrl,
                request,
                ProductResponse.class).getBody();
        assertThat(createdProduct).isNotNull();

        // When
        ResponseEntity<ProductResponse> response = restTemplate.getForEntity(
                baseUrl + "/" + createdProduct.id(),
                ProductResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(createdProduct.id());
        assertThat(response.getBody().name()).isEqualTo("Product to Retrieve");
    }

    @Test
    void getAllProducts_ShouldReturnProducts() {
        // Given
        ProductRequest request1 = new ProductRequest("Product 1", "Description 1", new BigDecimal("10.99"), true);
        ProductRequest request2 = new ProductRequest("Product 2", "Description 2", new BigDecimal("20.99"), true);
        
        restTemplate.postForEntity(baseUrl, request1, ProductResponse.class);
        restTemplate.postForEntity(baseUrl, request2, ProductResponse.class);

        // When
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {});

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = response.getBody();
        
        assertThat(responseBody.get("totalItems")).isEqualTo(2);
        assertThat(responseBody.get("currentPage")).isEqualTo(0);
        
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> products = (java.util.List<Map<String, Object>>) responseBody.get("products");
        
        assertThat(products).hasSize(2);
        assertThat(products.get(0).get("name")).isIn("Product 1", "Product 2");
        assertThat(products.get(1).get("name")).isIn("Product 1", "Product 2");
    }

    @Test
    void updateProduct_WhenProductExists_ShouldReturnUpdatedProduct() {
        // Given
        ProductRequest createRequest = new ProductRequest("Product to Update", "Original description", new BigDecimal("49.99"), true);
        ProductResponse createdProduct = restTemplate.postForEntity(
                baseUrl,
                createRequest,
                ProductResponse.class).getBody();
        assertThat(createdProduct).isNotNull();

        ProductRequest updateRequest = new ProductRequest("Updated Product", "Updated description", new BigDecimal("59.99"), true);

        // When
        ResponseEntity<ProductResponse> response = restTemplate.exchange(
                baseUrl + "/" + createdProduct.id(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                ProductResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(createdProduct.id());
        assertThat(response.getBody().name()).isEqualTo("Updated Product");
        assertThat(response.getBody().description()).isEqualTo("Updated description");
        assertThat(response.getBody().price()).isEqualTo(new BigDecimal("59.99"));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldReturnNoContent() {
        // Given
        ProductRequest request = new ProductRequest("Product to Delete", "Description for deletion test", new BigDecimal("69.99"), true);
        ProductResponse createdProduct = restTemplate.postForEntity(
                baseUrl,
                request,
                ProductResponse.class).getBody();
        assertThat(createdProduct).isNotNull();

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + createdProduct.id(),
                HttpMethod.DELETE,
                null,
                Void.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // Verify the product is deleted
        ResponseEntity<ProductResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + createdProduct.id(),
                ProductResponse.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}