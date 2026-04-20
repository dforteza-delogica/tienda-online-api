package com.delogica.tienda_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto 
{
    private Long    id;
    private String  line1;
    private String  line2;
    private String  city;
    private String  postalCode;
    private String  country;
    private Boolean isDefault;
}