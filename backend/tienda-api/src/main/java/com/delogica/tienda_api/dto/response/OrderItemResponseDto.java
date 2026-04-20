package com.delogica.tienda_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto 
{
    private Long    productoId;
    private Integer quantity;
    private Double  unitPrice;
}
