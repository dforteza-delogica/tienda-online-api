package com.delogica.tienda_api.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.delogica.tienda_api.domain.Address;
import com.delogica.tienda_api.domain.Customer;
import com.delogica.tienda_api.dto.response.CustomerResponseDto;

public interface CustomerService
{
    // CRUD
    Customer                    save(Customer customer);
    Customer                    findById(Long id);
    Page<CustomerResponseDto>   findAll(String email, Pageable pageable);
    Customer                    update(Customer customer);
    void                        deleteById(Long id);
    
    // ADDRESES
    Address                     addAddress(Long customerId, Address address);
    Address                     setDefaultAddress(Long customerId, Long addressId);
}
