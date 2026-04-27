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
        log.debug("Finding product by id: {}", id);
        
        return repository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found or inactive: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAll(String name, Pageable pageable) 
    {
        log.debug("Listing products - name filter: {}, page: {}", name, pageable.getPageNumber());

        Page<Product> page;

        if (name != null && !name.isEmpty())
            page = repository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable);
        else
            page = repository.findByActiveTrue(pageable);

        List<ProductResponseDto> dtos = page.getContent()
                .stream()
                .map(productMapper::toResponseDto)
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public Product save(Product product) 
    {
        log.info("Creating product with SKU: {}", product.getSku());

        if (repository.existsBySku(product.getSku())) 
        {
            log.error("Attempt to create product with duplicate SKU: {}", product.getSku());
            throw new ConflictException("Product with SKU: " + product.getSku() + " already exists");
        }

        Product saved = repository.save(product);
        log.info("Product created successfully - id: {}, SKU: {}", saved.getId(), saved.getSku());
        
        return saved;
    }

    @Override
    @Transactional
    public Product update(Product product)
    {
        Product updated = repository.save(product);
        log.info("Product updated successfully - id: {}", updated.getId());
        
        return updated;
    }

    @Override
    @Transactional
    public void deleteById(Long id) 
    {
        log.info("Soft deleting product id: {}", id);
        
        Product product = repository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found or inactive: " + id));
        
        product.setActive(false);
        repository.save(product);
        
        log.info("Product deactivated successfully - id: {}", id);
    }
}