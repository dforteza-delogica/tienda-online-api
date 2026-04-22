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
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) 
    {
        Product product = repository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado o inactivo: " + id));
        return (product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() 
    {
        return (repository.findByActiveTrue());
    }

    @Override
    @Transactional
    public Product save(Product product) 
    {
        if (repository.existsBySku(product.getSku()))
            throw new ConflictException("El producto con SKU: " + product.getSku() + " ya existe");

        return (repository.save(product));
    }

    @Override
    @Transactional
    public Product update(Product product)
    {
        // 1. VALIDAR QUE EXISTE Y ESTA ACTIVO
        Product existing = repository
                            .findByIdAndActiveTrue(product.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado o inactivo: " + product.getId()));

        // 2. UPDATE CAMPOS PERMITIDOS
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());

        // 3. UPDATE CAMBIOS
        return (repository.save(existing));
    }

    @Override
    @Transactional
    public void deleteById(Long id) 
    {
        // 1. VALIDAR EXISTENCIA POR ID
        Product product = repository
                            .findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        // 2. SOFT DELETE (INACTIVAR)
        product.setActive(false);

        // 3. UPDATE CAMBIOS
        repository.save(product);
    }

}
