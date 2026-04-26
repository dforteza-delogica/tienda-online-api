package com.delogica.tienda_api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delogica.tienda_api.domain.Product;
import com.delogica.tienda_api.dto.response.ProductResponseDto;
import com.delogica.tienda_api.exception.ConflictException;
import com.delogica.tienda_api.exception.ResourceNotFoundException;
import com.delogica.tienda_api.mapper.ProductMapper;
import com.delogica.tienda_api.repository.ProductRepository;
import com.delogica.tienda_api.service.interfaces.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService 
{
    private final ProductRepository repository;
    private final ProductMapper     productMapper;

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) 
    {
        log.debug("Buscando producto por id: {}", id);
        
        Product product = repository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado o inactivo: " + id));
        
        return (product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAll(String name, Pageable pageable) 
    {
        log.debug("Listando productos - filtro nombre: {}, página: {}", name, pageable.getPageNumber());

        // 1. OBTENER PAGE SEGÚN FILTROS
        Page<Product> page;

        if (name != null && !name.isEmpty())
            page = repository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable);
        else
            page = repository.findByActiveTrue(pageable);

        // 2. CONVERTIR A DTO
        List<ProductResponseDto> dtos = page.getContent()
                .stream()
                .map((product) -> productMapper.toResponseDto(product))
                .toList();

        // 3. DEVOLVER PAGE CON DTOs
        return (new PageImpl<>(
                    dtos,
                    pageable,
                    page.getTotalElements())
        );
    }

    @Override
    @Transactional
    public Product save(Product product) 
    {
        log.info("Creando producto con SKU: {}", product.getSku());

        // 1. VALIDAR SKKU
        if (repository.existsBySku(product.getSku())) 
        {
            log.warn("Intento de crear producto con SKU duplicado: {}", product.getSku());
            throw new ConflictException("El producto con SKU: " + product.getSku() + " ya existe");
        }

        // 2. PERSISTIR
        Product saved = repository.save(product);
        log.info("Producto creado exitosamente - id: {}, SKU: {}", saved.getId(), saved.getSku());
        
        return (saved);
    }

    @Override
    @Transactional
    public Product update(Product product)
    {
        Product updated = repository.save(product);
        log.info("Producto actualizado exitosamente - id: {}", updated.getId());
        
        return (updated);
    }


    @Override
    @Transactional
    public void deleteById(Long id) 
    {
        log.info("Eliminando (soft delete) producto id: {}", id);
        
        // 1. VALIDAR EXISTENCIA POR ID
        Product product = repository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado o inactivo: " + id));
        
        // 2. SOFT DELETE (INACTIVAR)
        product.setActive(false);

        // 3. UPDATE CAMBIOS
        repository.save(product);
        log.info("Producto desactivado exitosamente - id: {}", id);
    }

}