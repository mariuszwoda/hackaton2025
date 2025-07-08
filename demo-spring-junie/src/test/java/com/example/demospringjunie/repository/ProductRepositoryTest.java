package com.example.demospringjunie.repository;

import com.example.demospringjunie.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the ProductRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        // Clear the repository
        productRepository.deleteAll();

        // Create test products
        LocalDateTime now = LocalDateTime.now();

        product1 = new Product();
        product1.setName("Test Product 1");
        product1.setDescription("Description for test product 1");
        product1.setPrice(new BigDecimal("10.99"));
        product1.setCreatedAt(now);
        product1.setActive(true);

        product2 = new Product();
        product2.setName("Test Product 2");
        product2.setDescription("Description for test product 2");
        product2.setPrice(new BigDecimal("20.99"));
        product2.setCreatedAt(now);
        product2.setActive(true);

        product3 = new Product();
        product3.setName("Inactive Product");
        product3.setDescription("This product is not active");
        product3.setPrice(new BigDecimal("5.99"));
        product3.setCreatedAt(now);
        product3.setActive(false);

        // Save the products
        product1 = productRepository.save(product1);
        product2 = productRepository.save(product2);
        product3 = productRepository.save(product3);
    }

    @Test
    void findByName_ShouldReturnProduct() {
        // When
        Optional<Product> result = productRepository.findByName("Test Product 1");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(product1.getId());
        assertThat(result.get().getName()).isEqualTo("Test Product 1");
    }

    @Test
    void findByActiveTrue_ShouldReturnOnlyActiveProducts() {
        // When
        List<Product> result = productRepository.findByActiveTrue();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("Test Product 1", "Test Product 2");
    }

    @Test
    void findByActiveTrue_WithPagination_ShouldReturnPageOfActiveProducts() {
        // When
        Page<Product> result = productRepository.findByActiveTrue(PageRequest.of(0, 10));

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Product::getName)
                .containsExactlyInAnyOrder("Test Product 1", "Test Product 2");
    }

    @Test
    void findByPriceLessThanEqual_ShouldReturnProductsWithPriceLessThanOrEqualToSpecifiedValue() {
        // When
        List<Product> result = productRepository.findByPriceLessThanEqual(new BigDecimal("15.00"));

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("Test Product 1", "Inactive Product");
    }

    @Test
    void searchByText_ShouldReturnProductsContainingSpecifiedTextInNameOrDescription() {
        // When
        List<Product> result = productRepository.searchByText("test");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("Test Product 1", "Test Product 2");

        // When searching for a more specific term
        result = productRepository.searchByText("inactive");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Inactive Product");
    }
}
