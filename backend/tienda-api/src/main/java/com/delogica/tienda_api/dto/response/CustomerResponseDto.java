package com.delogica.tienda_api.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto 
{
    private Long                        id;
    private String                      fullName;
    private String                      email;
    private String                      phone;
    private List<AddressResponseDto>    addresses;
    private List<OrderResponseDto>      orders;
    private String                      createdAt;
    private String                      updatedAt;
}
