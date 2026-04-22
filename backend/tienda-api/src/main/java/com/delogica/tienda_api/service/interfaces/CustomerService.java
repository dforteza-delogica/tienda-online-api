package com.delogica.tienda_api.service.interfaces;

import java.util.List;

import com.delogica.tienda_api.domain.Address;
import com.delogica.tienda_api.domain.Customer;

public interface CustomerService
{
    // CRUD
    Customer            save(Customer customer);
    Customer            findById(Long id);
    List<Customer>      findAll();
    Customer            update(Customer customer);
    void                deleteById(Long id);
    
    // ADDRESES
    Address             addAddress(Long customerId, Address address);
    Address             setDefaultAddress(Long customerId, Long addressId);
}
