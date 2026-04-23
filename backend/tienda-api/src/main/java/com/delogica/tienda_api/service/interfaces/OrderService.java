package com.delogica.tienda_api.service.interfaces;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.delogica.tienda_api.domain.Order;
import com.delogica.tienda_api.domain.OrderStatus;
import com.delogica.tienda_api.dto.request.OrderRequestDto;
import com.delogica.tienda_api.dto.response.OrderResponseDto;

public interface OrderService
{
    Order                   save(OrderRequestDto dto);
    Order                   findById(Long id);;
    Page<OrderResponseDto>  findAll(Long customerId, OrderStatus status, Pageable pageable);
    Order                   updateStatus(Long id, OrderStatus newStatus);
}