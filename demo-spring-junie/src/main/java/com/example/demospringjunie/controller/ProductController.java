package com.example.demospringjunie.controller;

import com.example.demospringjunie.config.AppProperties;
import com.example.demospringjunie.dto.ProductRequest;
import com.example.demospringjunie.dto.ProductResponse;
import com.example.demospringjunie.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing products.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
class ProductController {

    private final ProductService productService;
    private final AppProperties appProperties;

    /**
     * Get all products with pagination.
     *
     * @param page page number (0-based)
     * @param size page size
     * @param sort sort field
     * @param direction sort direction
     * @param activeOnly whether to return only active products
     * @return a page of products
     */
    @GetMapping
    ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        int pageSize = size != null ? size : appProperties.getMaxItemsPerPage();
        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, sortDirection, sort);

        Page<ProductResponse> productPage = activeOnly
                ? productService.getActiveProducts(pageRequest)
                : productService.getAllProducts(pageRequest);

        Map<String, Object> response = Map.of(
                "products", productPage.getContent(),
                "currentPage", productPage.getNumber(),
                "totalItems", productPage.getTotalElements(),
                "totalPages", productPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get a product by ID.
     *
     * @param id the product ID
     * @return the product
     */
    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));
    }

    /**
     * Search products by text in name or description.
     *
     * @param query the search query
     * @return a list of products matching the search criteria
     */
    @GetMapping("/search")
    ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String query) {
        List<ProductResponse> products = productService.searchProducts(query);
        Map<String, Object> response = Map.of(
                "products", products,
                "count", products.size()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Find products with price less than or equal to the specified value.
     *
     * @param maxPrice the maximum price
     * @return a list of products with price less than or equal to the specified value
     */
    @GetMapping("/by-price")
    ResponseEntity<Map<String, Object>> getProductsByPrice(@RequestParam BigDecimal maxPrice) {
        List<ProductResponse> products = productService.findProductsByMaxPrice(maxPrice);
        Map<String, Object> response = Map.of(
                "products", products,
                "count", products.size()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new product.
     *
     * @param request the product request
     * @return the created product
     */
    @PostMapping
    ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse createdProduct = productService.createProduct(request);
        return ResponseEntity
                .created(URI.create("/api/v1/products/" + createdProduct.id()))
                .body(createdProduct);
    }

    /**
     * Update an existing product.
     *
     * @param id the product ID
     * @param request the product request
     * @return the updated product
     */
    @PutMapping("/{id}")
    ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));
    }

    /**
     * Delete a product by ID.
     *
     * @param id the product ID
     * @return no content if successful
     */
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id);
        }
        return ResponseEntity.noContent().build();
    }
}