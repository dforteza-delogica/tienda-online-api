package com.delogica.tienda_api.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delogica.tienda_api.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{
    // Sql:         SELECT * FROM product WHERE sku = :sku
    boolean existsBySku(String sku);

    // Endpoint:    GET /api/products/{id}
    // Sql:         SELECT * FROM product WHERE id = :id AND active = true
    Optional<Product> findByIdAndActiveTrue(Long id);

    // Endpoint:    GET /api/products
    // Sql:         SELECT * FROM product WHERE active = true
    Page<Product> findByActiveTrue(Pageable pageable);

    // Endpoint:    GET /api/products?name={name}
    // Sql:         SELECT * FROM products WHERE LOWER(name) LIKE LOWER('%:name%') AND active = true
    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);
}
