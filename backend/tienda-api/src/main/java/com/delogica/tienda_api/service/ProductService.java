package com.delogica.tienda_api.service;

import java.util.List;
import java.util.Optional;

import com.delogica.tienda_api.domain.Product;

public interface ProductService 
{
    Product             save(Product product);
    Optional<Product>   findById(Long id);
    List<Product>       findAll();
    void                deleteById(Long id);
}
