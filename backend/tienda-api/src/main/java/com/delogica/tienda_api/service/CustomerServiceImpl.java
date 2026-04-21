package com.delogica.tienda_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delogica.tienda_api.domain.Address;
import com.delogica.tienda_api.domain.Customer;
import com.delogica.tienda_api.exception.ConflictException;
import com.delogica.tienda_api.exception.InvalidOperationException;
import com.delogica.tienda_api.exception.ResourceNotFoundException;
import com.delogica.tienda_api.repository.AddressRepository;
import com.delogica.tienda_api.repository.CustomerRepository;
import com.delogica.tienda_api.service.interfaces.CustomerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService
{
    private final CustomerRepository    customerRepository;
    private final AddressRepository     addressRepository;

    @Override
    @Transactional(readOnly = true)
    public Customer findById(Long id)
    {
        // 1. BUSCAR O LANZAR EXCEPCION
        return customerRepository
                    .findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAll()
    {
        // 1. LISTAR TODO
        return (customerRepository.findAll());
    }

    @Override
    @Transactional
    public Customer save(Customer customer)
    {
        // 1. VALIDAR QUE EL EMAIL NO EXISTE
        if (customerRepository.existsByEmail(customer.getEmail()))
            throw new ConflictException("Ya existe un cliente con el email: " + customer.getEmail());

        // 2. PERSISTIR Y DEVOLVER
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer update(Customer customer)
    {
        // 1. VALIDAR EXISTENCIA
        if (!customerRepository.existsById(customer.getId()))
            throw new ResourceNotFoundException("Cliente no encontrado: " + customer.getId());

        // 2. PERSISTIR CAMBIOS
        return (customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void deleteById(Long id)
    {
        // 1. VALIDAR EXISTENCIA
        if (!customerRepository.existsById(id))
            throw new ResourceNotFoundException("Cliente no encontrado: " + id);

        // 2. ELIMINAR
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Address addAddress(Long customerId, Address address)
    {
        // 1. VALIDAR QUE EL CLIENTE EXISTE
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + customerId));

        // 2. SI ES DEFAULT, LIMPIAR EL DEFAULT ANTERIOR
        if (Boolean.TRUE.equals(address.isDefault()))
        {
            addressRepository.clearDefaultByCustomerId(customerId);
        }

        // 3. ASIGNAR CLIENTE A LA DIRECCION
        address.setCustomer(customer);

        // 4. PERSISTIR Y DEVOLVER
        return (addressRepository.save(address));
    }

    @Override
    @Transactional
    public Address setDefaultAddress(Long customerId, Long addressId)
    {
        // 1. VALIDAR QUE EL CLIENTE EXISTE
        customerRepository
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + customerId));

        // 2. VALIDAR QUE LA DIRECCION EXISTE Y PERTENECE AL CLIENTE
        Address address = addressRepository
                            .findByIdAndCustomerId(addressId, customerId)
                            .orElseThrow(() -> new InvalidOperationException("La dirección " + addressId + " no pertenece al cliente " + customerId));

        // 3. LIMPIAR DEFAULT ANTERIOR
        addressRepository.clearDefaultByCustomerId(customerId);

        // 4. MARCAR COMO DEFAULT
        address.setDefault(true);

        // 5. PERSISTIR Y DEVOLVER
        return (addressRepository.save(address));
    }
}