package com.example.demospringjunie.service;

import com.example.demospringjunie.domain.Product;
import com.example.demospringjunie.dto.ProductRequest;
import com.example.demospringjunie.dto.ProductResponse;
import com.example.demospringjunie.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for the ProductService.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        List<Product> products = List.of(
                createProduct(1L, "Product 1", "Description 1", new BigDecimal("10.99"), now, null, true),
                createProduct(2L, "Product 2", "Description 2", new BigDecimal("20.99"), now, null, true)
        );
        Page<Product> page = new PageImpl<>(products, PageRequest.of(0, 10), 2);
        
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        Page<ProductResponse> result = productService.getAllProducts(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).name()).isEqualTo("Product 1");
        assertThat(result.getContent().get(1).name()).isEqualTo("Product 2");
        
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    void getActiveProducts_ShouldReturnOnlyActiveProducts() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        List<Product> products = List.of(
                createProduct(1L, "Product 1", "Description 1", new BigDecimal("10.99"), now, null, true)
        );
        Page<Product> page = new PageImpl<>(products, PageRequest.of(0, 10), 1);
        
        when(productRepository.findByActiveTrue(any(Pageable.class))).thenReturn(page);

        // When
        Page<ProductResponse> result = productService.getActiveProducts(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Product 1");
        assertThat(result.getContent().get(0).active()).isTrue();
        
        verify(productRepository).findByActiveTrue(any(Pageable.class));
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Product product = createProduct(1L, "Product 1", "Description 1", new BigDecimal("10.99"), now, null, true);
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When
        Optional<ProductResponse> result = productService.getProductById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().name()).isEqualTo("Product 1");
        assertThat(result.get().description()).isEqualTo("Description 1");
        assertThat(result.get().price()).isEqualTo(new BigDecimal("10.99"));
        
        verify(productRepository).findById(1L);
    }

    @Test
    void createProduct_ShouldSaveAndReturnProduct() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        ProductRequest request = new ProductRequest("New Product", "New Description", new BigDecimal("15.99"), true);
        Product savedProduct = createProduct(1L, "New Product", "New Description", new BigDecimal("15.99"), now, null, true);
        
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponse result = productService.createProduct(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("New Product");
        assertThat(result.description()).isEqualTo("New Description");
        assertThat(result.price()).isEqualTo(new BigDecimal("15.99"));
        
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateAndReturnProduct() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        ProductRequest request = new ProductRequest("Updated Product", "Updated Description", new BigDecimal("25.99"), true);
        Product existingProduct = createProduct(1L, "Product 1", "Description 1", new BigDecimal("10.99"), now, null, true);
        Product updatedProduct = createProduct(1L, "Updated Product", "Updated Description", new BigDecimal("25.99"), now, now, true);
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // When
        Optional<ProductResponse> result = productService.updateProduct(1L, request);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().name()).isEqualTo("Updated Product");
        assertThat(result.get().description()).isEqualTo("Updated Description");
        assertThat(result.get().price()).isEqualTo(new BigDecimal("25.99"));
        
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteAndReturnTrue() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Product product = createProduct(1L, "Product 1", "Description 1", new BigDecimal("10.99"), now, null, true);
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(Product.class));

        // When
        boolean result = productService.deleteProduct(1L);

        // Then
        assertThat(result).isTrue();
        
        verify(productRepository).findById(1L);
        verify(productRepository).delete(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductDoesNotExist_ShouldReturnFalse() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        boolean result = productService.deleteProduct(1L);

        // Then
        assertThat(result).isFalse();
        
        verify(productRepository).findById(1L);
        verify(productRepository, never()).delete(any(Product.class));
    }

    private Product createProduct(Long id, String name, String description, BigDecimal price, 
                                 LocalDateTime createdAt, LocalDateTime updatedAt, boolean active) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCreatedAt(createdAt);
        product.setUpdatedAt(updatedAt);
        product.setActive(active);
        return product;
    }
}