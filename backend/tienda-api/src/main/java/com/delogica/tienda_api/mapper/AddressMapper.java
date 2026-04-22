package com.delogica.tienda_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.delogica.tienda_api.domain.Address;
import com.delogica.tienda_api.dto.request.AddressRequestDto;
import com.delogica.tienda_api.dto.response.AddressResponseDto;

@Mapper(componentModel = "spring")
public interface AddressMapper
{
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    Address toEntity(AddressRequestDto dto);
    
    AddressResponseDto toResponseDto(Address address);
}