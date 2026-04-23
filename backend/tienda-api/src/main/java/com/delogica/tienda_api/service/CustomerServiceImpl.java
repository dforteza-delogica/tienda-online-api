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
        log.debug("Buscando cliente por id: {}", id);

        // 1. DEVOLVER CLIENTE
        return (customerRepository
                    .findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDto> findAll(String email, Pageable pageable)
    {
        log.debug("Listando clientes - filtro email: {}, página: {}", email, pageable.getPageNumber());

        Page<Customer> page;

        // 1. OBTENER PAGE SEGÚN FILTROS
        if (email != null && !email.isEmpty())
            page = customerRepository.findByEmailContainingIgnoreCase(email, pageable);
        else
            page = customerRepository.findAll(pageable);

        // 2. CONVERTIR A DTO
        List<CustomerResponseDto> dtos = page.getContent()
                .stream()
                .map((customer) -> customerMapper.toResponseDto(customer))
                .toList();

        // 3. DEVOLVER PAGE CON DTOs
        return (new PageImpl<>(dtos, pageable, page.getTotalElements()));
    }

    @Override
    @Transactional
    public Customer save(Customer customer)
    {
        log.info("Creando cliente con email: {}", customer.getEmail());

        // 1. VALIDAR QUE EL EMAIL NO EXISTE
        if (customerRepository.existsByEmail(customer.getEmail())) {
            log.warn("Intento de crear cliente con email duplicado: {}", customer.getEmail());
            throw new ConflictException("Ya existe un cliente con el email: " + customer.getEmail());
        }

        // 2. PERSISTIR Y DEVOLVER
        Customer saved = customerRepository.save(customer);
        log.info("Cliente creado exitosamente - id: {}, email: {}", saved.getId(), saved.getEmail());
        
        return (saved);
    }

    @Override
    @Transactional
    public Customer update(Customer customer)
    {
        log.info("Actualizando cliente id: {}", customer.getId());

        // 1. VALIDAR EXISTENCIA
        Customer existing = customerRepository
                                .findById(customer.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + customer.getId()));

        // 2. VALIDAR EMAIL (NO PERTENECE A OTRO CLIENTE)
        if (!existing.getEmail().equals(customer.getEmail()) && customerRepository.existsByEmail(customer.getEmail())) {
            log.warn("Intento de actualizar cliente con email duplicado: {}", customer.getEmail());
            throw new ConflictException("Ya existe un cliente con el email: " + customer.getEmail());
        }

        // 3. PERSISTIR Y DEVOLVER
        Customer updated = customerRepository.save(customer);
        log.info("Cliente actualizado exitosamente - id: {}", updated.getId());
        
        return (updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id)
    {
        log.info("Eliminando cliente id: {}", id);

        // 1. VALIDAR EXISTENCIA
        if (!customerRepository.existsById(id))
            throw new ResourceNotFoundException("Cliente no encontrado: " + id);

        // 2. ELIMINAR
        customerRepository.deleteById(id);
        log.info("Cliente eliminado exitosamente - id: {}", id);
    }

    @Override
    @Transactional
    public Address addAddress(Long customerId, Address address)
    {
        log.info("Añadiendo dirección a cliente: {}, isDefault: {}", customerId, address.getIsDefault());

        // 1. VALIDAR QUE EL CLIENTE EXISTE
        Customer customer = customerRepository
                                .findById(customerId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + customerId));
        // 2. GARANTIZAR QUE isDefault NO es NULL
        if (address.getIsDefault() == null) 
            address.setIsDefault(false);

        // 3. SI ES DEFAULT, LIMPIAR DEFAULT ANTERIOR
        if (address.getIsDefault()) {
            log.debug("Limpiando dirección por defecto anterior del cliente: {}", customerId);
            addressRepository.clearDefaultByCustomerId(customerId);
        }

        // 4. ASOCIAR CON EL CLIENTE
        address.setCustomer(customer);

        // 5. PERSISTIR Y DEVOLVER
        Address saved = addressRepository.save(address);
        log.info("Dirección añadida exitosamente - id: {}, cliente: {}", saved.getId(), customerId);
        
        return (saved);
    }

    @Override
    @Transactional
    public Address setDefaultAddress(Long customerId, Long addressId)
    {
        log.info("Estableciendo dirección por defecto - cliente: {}, dirección: {}", customerId, addressId);

        // 1. VALIDAR QUE EL CLIENTE EXISTE
        customerRepository
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + customerId));

        // 2. VALIDAR QUE LA DIRECCION EXISTE
        Address address = addressRepository
                                .findById(addressId)
                                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada: " + addressId));

        // 3. VALIDAR QUE PERTENECE AL CLIENTE
        if (!address.getCustomer().getId().equals(customerId))
            throw new InvalidOperationException("La dirección " + addressId + " no pertenece al cliente " + customerId);

        // 4. LIMPIAR DEFAULT ANTERIOR
        addressRepository.clearDefaultByCustomerId(customerId);

        // 5. MARCAR COMO DEFAULT
        address.setIsDefault(true);

        // 6. PERSISTIR Y DEVOLVER
        Address updated = addressRepository.save(address);
        log.info("Dirección establecida como por defecto exitosamente - id: {}, cliente: {}", addressId, customerId);
        
        return (updated);
    }
}