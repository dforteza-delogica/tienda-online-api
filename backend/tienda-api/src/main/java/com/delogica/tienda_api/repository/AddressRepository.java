package com.delogica.tienda_api.repository;


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
    // Endpoint:    GET /api/customers/{customerId}/addresses/default
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.customer.id = :customerId")
    void clearDefaultByCustomerId(@Param("customerId") Long customerId);

}
