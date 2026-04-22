package com.delogica.tienda_api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.delogica.tienda_api.domain.Customer;
import com.delogica.tienda_api.dto.request.CustomerRequestDto;
import com.delogica.tienda_api.dto.response.CustomerResponseDto;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface CustomerMapper
{
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    Customer toEntity(CustomerRequestDto dto);

    CustomerResponseDto toResponseDto(Customer customer);

    List<CustomerResponseDto> toResponseDtoList(List<Customer> customers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    void updateCustomerFromDto(CustomerRequestDto dto, @MappingTarget Customer customer);
}
