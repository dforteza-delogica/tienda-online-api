package com.delogica.tienda_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delogica.tienda_api.domain.Product;
import com.delogica.tienda_api.exception.ConflictException;
import com.delogica.tienda_api.exception.ResourceNotFoundException;
import com.delogica.tienda_api.repository.ProductRepository;
import com.delogica.tienda_api.service.interfaces.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService
{
    private final ProductRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) 
    {
        Product product = repository
                                .findById(id)
                                .orElseThrow(() ->  new ResourceNotFoundException("Producto no encontrado: "+ id));
        return (product);
    }

    
    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() 
    {
        return (repository.findAll());
    }


    @Override
    @Transactional
    public Product save(Product product) 
    {
        if (repository.existsBySku(product.getSku()))
            throw new ConflictException("El producto con SKU: "+product.getSku()+" ya existe");
        
        return  (repository.save(product));
    }

    @Override
    @Transactional
    public Product update(Product product)
    {
        // 1. VALIDAR EXISTENCIA POR ID
        if (!repository.existsById(product.getId()))
            throw new ResourceNotFoundException("Producto no encontrada: " + product.getId());

        // 2. PERSISTIR CAMBIOS
        return repository.save(product);
    }



    @Override
    @Transactional
    public void deleteById(Long id) 
    {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException("Producto no encontrado" +id);

        repository.deleteById(id);
    }
    
}
