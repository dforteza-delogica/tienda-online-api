package com.delogica.tienda_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delogica.tienda_api.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{
    // Endpoint:    GET /api/products/sku/{sku}
    // Sql:         SELECT * FROM product WHERE sku = :sku
    boolean existsBySku(String sku);

    // Endpoint:    GET /api/products/{id}
    // Sql:         SELECT * FROM product WHERE id = :id AND active = true
    Optional<Product> findByIdAndActiveTrue(Long id);

    // Endpoint:    GET /api/products
    // Sql:         SELECT * FROM product WHERE active = true
    List<Product> findByActiveTrue();
}
