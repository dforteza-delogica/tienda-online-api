package com.delogica.tienda_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delogica.tienda_api.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    // Endpoint: GET /api/customers/email/{email}
    // Sql: SELECT * FROM customer WHERE email = :email
    Boolean existsByEmail(String email);

    // Sin filtros
    // Endpoint: GET /api/customers
    // Sql: SELECT * FROM customer
    Page<Customer> findAll(Pageable pageable);

    // Con filtro por email
    // Endpoint: GET /api/customers?email={email}
    // Sql: SELECT * FROM customer WHERE email LIKE %:email%
    Page<Customer> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}