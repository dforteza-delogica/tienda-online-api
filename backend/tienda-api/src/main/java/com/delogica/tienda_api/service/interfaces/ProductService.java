package com.delogica.tienda_api.service.interfaces;

import java.util.List;

import com.delogica.tienda_api.domain.Product;

public interface ProductService 
{
    Product         save(Product product);
    Product         findById(Long id);
    List<Product>   findAll();
    Product         update(Product product);
    void            deleteById(Long id);
}
