package com.delogica.tienda_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.delogica.tienda_api.domain.Address;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long>
{
    // Endpoint:    GET /api/customers/{customerId}/addresses/{id}
    // Sql:         SELECT * FROM address WHERE id = :id AND customer_id = :customerId
    Optional<Address>   findByIdAndCustomerId(Long id, Long customerId);
    
    // Endpoint:    GET /api/customers/{customerId}/addresses/default
    // Sql:         SELECT * FROM address WHERE customer_id = :customerId AND is_default = true
    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.customer.id = :customerId")
    Void clearDefaultByCustomerId(@Param("customerId") Long customerId);

}
