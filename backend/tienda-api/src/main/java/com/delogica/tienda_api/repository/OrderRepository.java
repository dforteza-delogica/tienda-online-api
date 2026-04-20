package com.delogica.tienda_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delogica.tienda_api.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long>
{
    // Endpoint:    GET /api/v1/customers/{customerId}/orders
    // Sql:         SELECT * FROM orders WHERE customer_id = :customerId
    List<Order> findByCustomerId(Long customerId);
}
