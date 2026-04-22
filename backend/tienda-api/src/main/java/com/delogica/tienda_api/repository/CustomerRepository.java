package com.delogica.tienda_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delogica.tienda_api.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    // Endpoint: GET /api/customers/email/{email}
    // Sql: SELECT * FROM customer WHERE email = :email
    Boolean existsByEmail(String email);

    
    // Endpoint: GET /api/customers/email/{email}
    // Sql: SELECT * FROM customer WHERE email = :email
    Optional<Customer> findByEmail(String email);
}