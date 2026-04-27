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

    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(@Valid @RequestBody CustomerRequestDto dto)
    {
        Customer toCreate = customerMapper.toEntity(dto);
        Customer saved = customerService.save(toCreate);
        CustomerResponseDto response = customerMapper.toResponseDto(saved);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getById(@PathVariable Long id)
    {
        Customer found = customerService.findById(id);
        CustomerResponseDto response = customerMapper.toResponseDto(found);

        return (new ResponseEntity<>(response, HttpStatus.OK));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponseDto>> getAll(
            @RequestParam(required = false) String email,
            Pageable pageable
    )
    {
        Page<CustomerResponseDto> response = customerService.findAll(email, pageable);

        return (new ResponseEntity<>(response, HttpStatus.OK));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto dto)
    {
        Customer existing = customerService.findById(id);
        customerMapper.updateCustomerFromDto(dto, existing);
        Customer updated = customerService.update(existing);
        CustomerResponseDto response = customerMapper.toResponseDto(updated);

        return (new ResponseEntity<>(response, HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        customerService.deleteById(id);

        return (new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressResponseDto> addAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDto dto)
    {
        Address toCreate = addressMapper.toEntity(dto);
        Address saved = customerService.addAddress(id, toCreate);
        AddressResponseDto response = addressMapper.toResponseDto(saved);

        return (new ResponseEntity<>(response, HttpStatus.CREATED));
    }

    @PutMapping("/{id}/addresses/{addressId}/default")
    public ResponseEntity<AddressResponseDto> setDefaultAddress(
            @PathVariable Long id,
            @PathVariable Long addressId)
    {
        Address updated = customerService.setDefaultAddress(id, addressId);
        AddressResponseDto response = addressMapper.toResponseDto(updated);

        return (new ResponseEntity<>(response, HttpStatus.OK));
    }
}