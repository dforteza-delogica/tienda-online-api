package com.delogica.tienda_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.delogica.tienda_api.domain.Order;
import com.delogica.tienda_api.domain.OrderStatus;
import com.delogica.tienda_api.dto.request.OrderRequestDto;
import com.delogica.tienda_api.dto.request.OrderStatusRequestDto;
import com.delogica.tienda_api.dto.response.OrderResponseDto;
import com.delogica.tienda_api.mapper.OrderMapper;
import com.delogica.tienda_api.service.interfaces.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController
{
    private final OrderService  orderService;
    private final OrderMapper   orderMapper;

    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto dto)
    {
        Order saved = orderService.save(dto);
        OrderResponseDto response = orderMapper.toResponseDto(saved);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Long id)
    {
        Order found = orderService.findById(id);
        OrderResponseDto response = orderMapper.toResponseDto(found);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getAll(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(
                page = 0, size = 10,
                sort = "orderDate", direction = Sort.Direction.DESC
            ) Pageable pageable
    )
    {
        Page<OrderResponseDto> response = orderService.findAll(customerId, status, pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequestDto dto)
    {
        Order updated = orderService.updateStatus(id, dto.getStatus());
        OrderResponseDto response = orderMapper.toResponseDto(updated);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}