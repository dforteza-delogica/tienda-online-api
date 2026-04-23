package com.delogica.tienda_api.service.interfaces;

import java.util.List;

import com.delogica.tienda_api.domain.Order;
import com.delogica.tienda_api.domain.OrderStatus;
import com.delogica.tienda_api.dto.request.OrderRequestDto;

public interface OrderService
{
    Order       save(OrderRequestDto dto);
    Order       findById(Long id);
    List<Order> findByCustomerId(Long customerId);
    List<Order> findAll();
    Order       updateStatus(Long id, OrderStatus newStatus);
}