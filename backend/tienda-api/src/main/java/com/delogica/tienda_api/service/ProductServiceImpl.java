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
        // 1.BUSCAR PRODUCTO ACTIVO POR ID
        Product existing = repository
                            .findByIdAndActiveTrue(product.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado o inactivo: " + product.getId())
            );

        // 2. Aplicar cambios controladamente
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setDescription(product.getDescription());

        // 3. GUARDAR CAMBIOS
        return (existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) 
    {
        //SOFT DELETE: DESACTIVAR EL PRODUCTO EN LUGAR DE ELIMINARLO FISICAMENTE
        // 1. VALIDAR EXISTENCIA POR ID
        Product product = repository
                            .findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        // 2. DESACTIVAR EL PRODUCTO
        product.setActive(false);

        // 3. UPDATE CAMBIOS
        repository.save(product);
    }

}
