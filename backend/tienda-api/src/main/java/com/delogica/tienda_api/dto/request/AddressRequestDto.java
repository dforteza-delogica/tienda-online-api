package com.delogica.tienda_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDto 
{
    @NotBlank(message = "La direccion es obligatoria")
    private String line1;

    private String line2;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "El código postal es obligatorio")
    private String postalCode;

    @NotBlank(message = "El país es obligatorio")
    private String country;

    private Boolean isDefault = false;
}
