package com.delogica.tienda_api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delogica.tienda_api.domain.Address;
import com.delogica.tienda_api.domain.Customer;
import com.delogica.tienda_api.dto.response.CustomerResponseDto;
import com.delogica.tienda_api.exception.ConflictException;
import com.delogica.tienda_api.exception.InvalidOperationException;
import com.delogica.tienda_api.exception.ResourceNotFoundException;
import com.delogica.tienda_api.mapper.CustomerMapper;
import com.delogica.tienda_api.repository.AddressRepository;
import com.delogica.tienda_api.repository.CustomerRepository;
import com.delogica.tienda_api.service.interfaces.CustomerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService
{
    private final CustomerRepository    customerRepository;
    private final AddressRepository     addressRepository;
    private final CustomerMapper        customerMapper; 

    @Override
    @Transactional(readOnly = true)
    public Customer findById(Long id)
    {
        log.debug("Finding customer by id: {}", id);

        return (customerRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDto> findAll(String email, Pageable pageable)
    {
        log.debug("Listing customers - email filter: {}, page: {}", email, pageable.getPageNumber());

        Page<Customer> page;

        if (email != null && !email.isEmpty())
            page = customerRepository.findByEmailContainingIgnoreCase(email, pageable);
        else
            page = customerRepository.findAll(pageable);

        List<CustomerResponseDto> dtos = page.getContent()
                .stream()
                .map(customerMapper::toResponseDto)
                .toList();

        return (new PageImpl<>(dtos, pageable, page.getTotalElements()));
    }

    @Override
    @Transactional
    public Customer save(Customer customer)
    {
        log.info("Creating customer with email: {}", customer.getEmail());

        if (customerRepository.existsByEmail(customer.getEmail())) 
        {
            log.error("Attempt to create customer with duplicate email: {}", customer.getEmail());
            throw new ConflictException("Customer with email already exists: " + customer.getEmail());
        }

        Customer saved = customerRepository.save(customer);
        log.info("Customer created successfully - id: {}, email: {}", saved.getId(), saved.getEmail());
        
        return (saved);
    }

    @Override
    @Transactional
    public Customer update(Customer customer)
    {
        log.info("Updating customer id: {}", customer.getId());

        Customer existing = customerRepository
                .findById(customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customer.getId()));

        if (!existing.getEmail().equals(customer.getEmail()) && customerRepository.existsByEmail(customer.getEmail())) 
        {
            log.error("Attempt to update customer with duplicate email: {}", customer.getEmail());
            throw new ConflictException("Customer with email already exists: " + customer.getEmail());
        }

        Customer updated = customerRepository.save(customer);
        log.info("Customer updated successfully - id: {}", updated.getId());
        
        return (updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id)
    {
        log.info("Deleting customer id: {}", id);

        if (!customerRepository.existsById(id))
            throw new ResourceNotFoundException("Customer not found: " + id);

        customerRepository.deleteById(id);
        log.info("Customer deleted successfully - id: {}", id);
    }

    @Override
    @Transactional
    public Address addAddress(Long customerId, Address address)
    {
        log.info("Adding address to customer: {}, isDefault: {}", customerId, address.getIsDefault());

        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        
        if (address.getIsDefault() == null) 
            address.setIsDefault(false);

        if (address.getIsDefault()) 
        {
            log.debug("Clearing previous default address for customer: {}", customerId);
            addressRepository.clearDefaultByCustomerId(customerId);
        }

        address.setCustomer(customer);

        Address saved = addressRepository.save(address);
        log.info("Address added successfully - id: {}, customer: {}", saved.getId(), customerId);
        
        return (saved);
    }

    @Override
    @Transactional
    public Address setDefaultAddress(Long customerId, Long addressId)
    {
        log.info("Setting default address - customer: {}, address: {}", customerId, addressId);

        customerRepository
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));

        Address address = addressRepository
                .findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));

        if (!address.getCustomer().getId().equals(customerId))
            throw new InvalidOperationException("Address " + addressId + " does not belong to customer " + customerId);

        addressRepository.clearDefaultByCustomerId(customerId);

        address.setIsDefault(true);

        Address updated = addressRepository.save(address);
        log.info("Address set as default successfully - id: {}, customer: {}", addressId, customerId);
        
        return (updated);
    }
}