package com.delogica.tienda_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delogica.tienda_api.domain.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>
{
    // Endpoint:    GET /api/customers/{customerId}/addresses
    // Sql:         SELECT * FROM address WHERE customer_id = :customerId
    List<Address>       findByCustomerId(Long customerId);

    // Endpoint:    GET /api/customers/{customerId}/addresses/{id}
    // Sql:         SELECT * FROM address WHERE id = :id AND customer_id = :customerId
    Optional<Address>   findByIdAndCustomerId(Long id, Long customerId);
    
}
