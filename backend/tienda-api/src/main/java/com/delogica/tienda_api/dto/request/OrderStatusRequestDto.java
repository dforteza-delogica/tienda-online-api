package com.delogica.tienda_api.dto.request;

import com.delogica.tienda_api.domain.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusRequestDto 
{
    @NotNull(message = "El estado es obligatorio")
    private OrderStatus status;
    
}
