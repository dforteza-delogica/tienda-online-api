package com.delogica.tienda_api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.delogica.tienda_api.domain.Order;
import com.delogica.tienda_api.domain.OrderItem;
import com.delogica.tienda_api.dto.response.OrderItemResponseDto;
import com.delogica.tienda_api.dto.response.OrderResponseDto;

@Mapper(componentModel = "spring")
public interface OrderMapper
{
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "shippingAddress.id", target = "shippingAddressId")
    OrderResponseDto toResponseDto(Order order);

    List<OrderResponseDto> toResponseDtoList(List<Order> orders);

    @Mapping(source = "product.id", target = "productId")
    OrderItemResponseDto toItemResponseDto(OrderItem item);
}