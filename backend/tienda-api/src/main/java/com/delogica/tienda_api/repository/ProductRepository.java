package com.delogica.tienda_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.delogica.tienda_api.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long>
{
    // Endpoint:    GET /api/products/sku/{sku}
    // Sql:         SELECT * FROM product WHERE sku = :sku
    boolean existsBySku(String sku);
}
