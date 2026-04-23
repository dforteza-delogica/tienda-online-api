package com.delogica.tienda_api.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto 
{
    @NotNull(message = "El cliente es obligatorio")
    private Long customerId;

    @NotNull(message = "La dirección de envío es obligatoria")
    private Long addressId;

    @NotNull(message = "El pedido debe contener al menos un producto")
    @NotEmpty(message ="El pedido debe contener al menos 1 producto")
    @Valid
    private List<OrderItemRequestDto> items;
    
}
