package com.delogica.tienda_api.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.delogica.tienda_api.domain.Address;
import com.delogica.tienda_api.domain.Customer;
import com.delogica.tienda_api.dto.request.AddressRequestDto;
import com.delogica.tienda_api.dto.request.CustomerRequestDto;
import com.delogica.tienda_api.dto.response.AddressResponseDto;
import com.delogica.tienda_api.dto.response.CustomerResponseDto;
import com.delogica.tienda_api.mapper.AddressMapper;
import com.delogica.tienda_api.mapper.CustomerMapper;
import com.delogica.tienda_api.service.interfaces.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController
{
    private final CustomerService   customerService;
    private final CustomerMapper    customerMapper;
    private final AddressMapper     addressMapper;

    // 1. CREATE
    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(@Valid @RequestBody CustomerRequestDto dto)
    {
        // 1. MAPEAR DTO A ENTITY
        Customer toCreate = customerMapper.toEntity(dto);

        // 2. PERSISTIR EN SERVICE
        Customer saved = customerService.save(toCreate);

        // 3. MAPEAR ENTITY A DTO
        CustomerResponseDto response = customerMapper.toResponseDto(saved);

        // 4. RESPONDER
        return (new ResponseEntity<>(response, HttpStatus.CREATED));
    }

    // 2. READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getById(@PathVariable Long id)
    {
        // 1. BUSCAR ENTITY EN SERVICE
        Customer found = customerService.findById(id);

        // 2. MAPEAR ENTITY A DTO
        CustomerResponseDto response = customerMapper.toResponseDto(found);

        // 3. RESPONDER
        return (new ResponseEntity<>(response, HttpStatus.OK));
    }

    // 3. READ ALL
    @GetMapping
    public ResponseEntity<Page<CustomerResponseDto>> getAll(
            @RequestParam(required = false) String email,
            @RequestParam Pageable pageable
    )
    {
        // 1. OBTENER PAGE DE SERVICE
        Page<CustomerResponseDto> response = customerService.findAll(email, pageable);

        // 3. RESPONDER
        return (new ResponseEntity<>(response, HttpStatus.OK));
    }

    // 4. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto dto)
    {
        // 1. CARGAR ENTITY ACTUAL
        Customer existing = customerService.findById(id);

        // 2. APLICAR CAMBIOS CON MAPPER
        customerMapper.updateCustomerFromDto(dto, existing);

        // 3. PERSISTIR EN SERVICE
        Customer updated = customerService.update(existing);

        // 4. MAPEAR ENTITY A DTO
        CustomerResponseDto response = customerMapper.toResponseDto(updated);

        // 5. RESPONDER
        return (new ResponseEntity<>(response, HttpStatus.OK));
    }

    // 5. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        // 1. ELIMINAR EN SERVICE
        customerService.deleteById(id);

        // 2. RESPONDER SIN CONTENIDO
        return (new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    // 6. ADD ADDRESS
    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressResponseDto> addAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDto dto)
    {
        // 1. MAPEAR DTO A ENTITY
        Address toCreate = addressMapper.toEntity(dto);

        // 2. PERSISTIR EN SERVICE
        Address saved = customerService.addAddress(id, toCreate);

        // 3. MAPEAR ENTITY A DTO
        AddressResponseDto response = addressMapper.toResponseDto(saved);

        // 4. RESPONDER
        return (new ResponseEntity<>(response, HttpStatus.CREATED));
    }

    // 7. SET DEFAULT ADDRESS
    @PutMapping("/{id}/addresses/{addressId}/default")
    public ResponseEntity<AddressResponseDto> setDefaultAddress(
            @PathVariable Long id,
            @PathVariable Long addressId)
    {
        // 1. EJECUTAR EN SERVICE
        Address updated = customerService.setDefaultAddress(id, addressId);

        // 2. MAPEAR ENTITY A DTO
        AddressResponseDto response = addressMapper.toResponseDto(updated);

        // 3. RESPONDER
        return (new ResponseEntity<>(response, HttpStatus.OK));
    }
}