package com.delogica.tienda_api.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.delogica.tienda_api.domain.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto 
{
    private Long            id;
    private Long            customerId;
    private Long            shippingAddressId;
    private LocalDateTime   orderDate;
    private OrderStatus     status;
    private Double          total;

    private List<OrderItemResponseDto> items;
}
