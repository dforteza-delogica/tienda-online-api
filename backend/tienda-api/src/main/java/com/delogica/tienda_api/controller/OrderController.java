package com.delogica.tienda_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delogica.tienda_api.dto.response.OrderResponseDto;
import com.delogica.tienda_api.mapper.OrderMapper;
import com.delogica.tienda_api.service.interfaces.OrderService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


import com.delogica.tienda_api.domain.Order;
import com.delogica.tienda_api.dto.request.OrderRequestDto;
import com.delogica.tienda_api.dto.request.OrderStatusRequestDto;


import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController
{
    private final OrderService  orderService;
    private final OrderMapper   orderMapper;

    // 1. CREATE
    // POST /api/orders
    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto dto)
    {
        // 1. PERSISTIR EN SERVICE — el service recibe el DTO directamente
        Order saved = orderService.save(dto);

        // 2. MAPEAR ENTITY A DTO
        OrderResponseDto response = orderMapper.toResponseDto(saved);

        // 3. RESPONDER 201
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. READ BY ID
    // GET /api/orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Long id)
    {
        // 1. BUSCAR EN SERVICE
        Order found = orderService.findById(id);

        // 2. MAPEAR ENTITY A DTO
        OrderResponseDto response = orderMapper.toResponseDto(found);

        // 3. RESPONDER 200
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 3. READ ALL
    // GET /api/orders
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAll()
    {
        // 1. OBTENER LISTA
        List<Order> orders = orderService.findAll();

        // 2. MAPEAR LISTA A DTO
        List<OrderResponseDto> response = orderMapper.toResponseDtoList(orders);

        // 3. RESPONDER 200
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 4. READ BY CUSTOMER
    // GET /api/orders/customer/{customerId}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDto>> getByCustomer(@PathVariable Long customerId)
    {
        // 1. OBTENER LISTA DEL CLIENTE
        List<Order> orders = orderService.findByCustomerId(customerId);

        // 2. MAPEAR LISTA A DTO
        List<OrderResponseDto> response = orderMapper.toResponseDtoList(orders);

        // 3. RESPONDER 200
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 5. UPDATE STATUS
    // PUT /api/orders/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequestDto dto)
    {
        // 1. EJECUTAR EN SERVICE
        Order updated = orderService.updateStatus(id, dto.getStatus());

        // 2. MAPEAR ENTITY A DTO
        OrderResponseDto response = orderMapper.toResponseDto(updated);

        // 3. RESPONDER 200
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}