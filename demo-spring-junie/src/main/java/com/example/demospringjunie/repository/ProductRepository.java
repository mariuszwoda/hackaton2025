package com.example.demospringjunie.repository;

import com.example.demospringjunie.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Product entities.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find a product by its name.
     *
     * @param name the product name
     * @return an Optional containing the product if found
     */
    Optional<Product> findByName(String name);

    /**
     * Find all active products.
     *
     * @return a list of active products
     */
    List<Product> findByActiveTrue();

    /**
     * Find all active products with pagination.
     *
     * @param pageable pagination information
     * @return a page of active products
     */
    Page<Product> findByActiveTrue(Pageable pageable);

    /**
     * Find products with price less than or equal to the specified value.
     *
     * @param price the maximum price
     * @return a list of products with price less than or equal to the specified value
     */
    List<Product> findByPriceLessThanEqual(BigDecimal price);

    /**
     * Find products containing the specified text in their name or description.
     *
     * @param text the text to search for
     * @return a list of products matching the search criteria
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Product> searchByText(@Param("text") String text);
}