package com.delogica.tienda_api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.delogica.tienda_api.domain.Product;
import com.delogica.tienda_api.dto.request.ProductRequestDto;
import com.delogica.tienda_api.dto.response.ProductResponseDto;
import com.delogica.tienda_api.dto.update.ProductUpdateDto;
import com.delogica.tienda_api.mapper.ProductMapper;
import com.delogica.tienda_api.service.interfaces.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController
{
    private final ProductService    productService;
    private final ProductMapper     productMapper;

    // 1. CREATE
    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto dto)
    {
        // 1. MAPEAR DTO A ENTITY
        Product toCreate = productMapper.toEntity(dto);

        // 2. PERSISTIR EN SERVICE
        Product saved = productService.save(toCreate);

        // 3. MAPEAR ENTITY A DTO
        ProductResponseDto response = productMapper.toResponseDto(saved);

        // 4. RESPONDER
        return (new ResponseEntity<>(response, HttpStatus.CREATED));
    }

    // 2. READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id)
    {
        // 1. BUSCAR ENTITY EN SERVICE
        Product found = productService.findById(id);

        // 2. MAPEAR ENTITY A DTO
        ProductResponseDto response = productMapper.toResponseDto(found);

        // 3. RESPONDER
        return (new ResponseEntity<>(response, HttpStatus.OK));
    }

    // 3. READ ALL
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAll(
        @RequestParam(required = false) String name,
        @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    )
    {
        // 1. OBTENER PAGE
        Page<ProductResponseDto> response = productService.findAll(name, pageable); 

        // 2. RESPONDER
        return (new ResponseEntity<>(response, HttpStatus.OK));
    }

    // 4. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDto dto)
    {
        // 1. CARGAR ENTITY ACTUAL
        Product existing = productService.findById(id);

        // 2. APLICAR CAMBIOS CON MAPPER
        productMapper.updateProductFromDto(dto, existing);

        // 3. PERSISTIR EN SERVICE
        Product updated = productService.update(existing);

        // 4. MAPEAR ENTITY A DTO
        ProductResponseDto response = productMapper.toResponseDto(updated);

        // 5. RESPONDER
        return (new ResponseEntity<>(response, HttpStatus.OK));
    }

    // 5. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        // 1. ELIMINAR EN SERVICE
        productService.deleteById(id);

        // 2. RESPONDER SIN CONTENIDO
        return (new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}