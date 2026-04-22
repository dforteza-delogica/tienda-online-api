package com.delogica.tienda_api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.delogica.tienda_api.domain.Product;
import com.delogica.tienda_api.dto.request.ProductRequestDto;
import com.delogica.tienda_api.dto.response.ProductResponseDto;
import com.delogica.tienda_api.dto.update.ProductUpdateDto;

@Mapper(componentModel = "spring")
public interface ProductMapper 
{
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductRequestDto dto);

    ProductResponseDto toResponseDto(Product product);
    List<ProductResponseDto> toResponseDtoList(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProductFromDto(ProductUpdateDto dto, @MappingTarget Product product);
}