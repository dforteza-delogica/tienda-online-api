package com.delogica.tienda_api.controller;

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

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto dto)
    {
        Product toCreate = productMapper.toEntity(dto);
        Product saved = productService.save(toCreate);
        ProductResponseDto response = productMapper.toResponseDto(saved);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id)
    {
        Product found = productService.findById(id);
        ProductResponseDto response = productMapper.toResponseDto(found);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAll(
        @RequestParam(required = false) String name,
        @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    )
    {
        Page<ProductResponseDto> response = productService.findAll(name, pageable); 

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDto dto)
    {
        Product existing = productService.findById(id);
        productMapper.updateProductFromDto(dto, existing);
        Product updated = productService.update(existing);
        ProductResponseDto response = productMapper.toResponseDto(updated);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        productService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}