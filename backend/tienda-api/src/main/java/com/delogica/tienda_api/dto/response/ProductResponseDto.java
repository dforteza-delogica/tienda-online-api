package com.delogica.tienda_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto 
{
    private Long    id;
    private String  sku;
    private String  name;
    private String  description;
    private String  price;
    private Integer stock;
    private Boolean active;
    private String  createdAt;
    private String  updatedAt;
}
