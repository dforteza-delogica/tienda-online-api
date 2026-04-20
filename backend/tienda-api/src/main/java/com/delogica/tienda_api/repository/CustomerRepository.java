package com.delogica.tienda_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delogica.tienda_api.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    // Endpoint: GET /api/customers/email/{email}
    // Sql: SELECT * FROM customer WHERE email = :email
    boolean existsByEmail(String email);
}