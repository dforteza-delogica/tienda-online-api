package com.delogica.tienda_api.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.delogica.tienda_api.domain.Product;
import com.delogica.tienda_api.dto.response.ProductResponseDto;

public interface ProductService 
{
    Product                     save(Product product);
    Product                     findById(Long id);
    Page<ProductResponseDto>    findAll(String name, Pageable pageable);
    Product                     update(Product product);
    void                        deleteById(Long id);
}
