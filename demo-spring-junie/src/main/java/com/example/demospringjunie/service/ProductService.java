package com.example.demospringjunie.service;

import com.example.demospringjunie.domain.Product;
import com.example.demospringjunie.dto.ProductRequest;
import com.example.demospringjunie.dto.ProductResponse;
import com.example.demospringjunie.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing products.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Get all products with pagination.
     *
     * @param pageable pagination information
     * @return a page of product responses
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToProductResponse);
    }

    /**
     * Get active products with pagination.
     *
     * @param pageable pagination information
     * @return a page of active product responses
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(this::mapToProductResponse);
    }

    /**
     * Get a product by ID.
     *
     * @param id the product ID
     * @return an Optional containing the product response if found
     */
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToProductResponse);
    }

    /**
     * Search products by text in name or description.
     *
     * @param text the search text
     * @return a list of product responses matching the search criteria
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String text) {
        return productRepository.searchByText(text).stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    /**
     * Find products with price less than or equal to the specified value.
     *
     * @param maxPrice the maximum price
     * @return a list of product responses with price less than or equal to the specified value
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findProductsByMaxPrice(BigDecimal maxPrice) {
        return productRepository.findByPriceLessThanEqual(maxPrice).stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    /**
     * Create a new product.
     *
     * @param request the product request
     * @return the created product response
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setActive(request.active() != null ? request.active() : true);
        product.setCreatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    /**
     * Update an existing product.
     *
     * @param id the product ID
     * @param request the product request
     * @return an Optional containing the updated product response if found
     */
    @Transactional
    public Optional<ProductResponse> updateProduct(Long id, ProductRequest request) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(request.name());
                    product.setDescription(request.description());
                    product.setPrice(request.price());
                    if (request.active() != null) {
                        product.setActive(request.active());
                    }
                    product.setUpdatedAt(LocalDateTime.now());
                    return mapToProductResponse(productRepository.save(product));
                });
    }

    /**
     * Delete a product by ID.
     *
     * @param id the product ID
     * @return true if the product was deleted, false otherwise
     */
    @Transactional
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Map a Product entity to a ProductResponse DTO.
     *
     * @param product the product entity
     * @return the product response DTO
     */
    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.isActive()
        );
    }
}