package com.example.demospringjunie.controller;

import com.example.demospringjunie.config.AppProperties;
import com.example.demospringjunie.dto.ProductRequest;
import com.example.demospringjunie.dto.ProductResponse;
import com.example.demospringjunie.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the ProductController.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private AppProperties appProperties;

    @Test
    void getAllProducts_ShouldReturnProducts() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        List<ProductResponse> products = List.of(
                new ProductResponse(1L, "Product 1", "Description 1", new BigDecimal("10.99"), now, null, true),
                new ProductResponse(2L, "Product 2", "Description 2", new BigDecimal("20.99"), now, null, true)
        );
        PageImpl<ProductResponse> page = new PageImpl<>(products, PageRequest.of(0, 10), 2);
        
        when(appProperties.getMaxItemsPerPage()).thenReturn(10);
        when(productService.getAllProducts(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(2)))
                .andExpect(jsonPath("$.totalItems", is(2)))
                .andExpect(jsonPath("$.products[0].id", is(1)))
                .andExpect(jsonPath("$.products[0].name", is("Product 1")))
                .andExpect(jsonPath("$.products[1].id", is(2)))
                .andExpect(jsonPath("$.products[1].name", is("Product 2")));
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        ProductResponse product = new ProductResponse(1L, "Product 1", "Description 1", new BigDecimal("10.99"), now, null, true);
        
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        // When & Then
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product 1")))
                .andExpect(jsonPath("$.description", is("Description 1")))
                .andExpect(jsonPath("$.price", is(10.99)));
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        ProductRequest request = new ProductRequest("New Product", "New Description", new BigDecimal("15.99"), true);
        ProductResponse response = new ProductResponse(1L, "New Product", "New Description", new BigDecimal("15.99"), now, null, true);
        
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/products/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.description", is("New Description")))
                .andExpect(jsonPath("$.price", is(15.99)));
    }

    @Test
    void updateProduct_WhenProductExists_ShouldReturnUpdatedProduct() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        ProductRequest request = new ProductRequest("Updated Product", "Updated Description", new BigDecimal("25.99"), true);
        ProductResponse response = new ProductResponse(1L, "Updated Product", "Updated Description", new BigDecimal("25.99"), now, now, true);
        
        when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(Optional.of(response));

        // When & Then
        mockMvc.perform(put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.price", is(25.99)));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(productService.deleteProduct(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }
}