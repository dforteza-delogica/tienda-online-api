package com.delogica.tienda_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.delogica.tienda_api.domain.Product;
import com.delogica.tienda_api.exception.ConflictException;
import com.delogica.tienda_api.exception.ResourceNotFoundException;
import com.delogica.tienda_api.mapper.ProductMapper;
import com.delogica.tienda_api.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest
{
    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp()
    {
        product = Product.builder()
                .id(1L)
                .sku("P-100")
                .name("Café Premium 250g")
                .price(new BigDecimal("7.50"))
                .stock(100)
                .active(true)
                .build();
    }

    // SAVE 

    @Test
    @DisplayName("Debe guardar un producto correctamente")
    void shouldSaveProductSuccessfully()
    {
        // Arrange
        when(repository.existsBySku("P-100")).thenReturn(false);
        when(repository.save(any(Product.class))).thenReturn(product);

        // Act
        Product saved = productService.save(product);

        // Assert
        assertNotNull(saved);
        assertEquals("P-100", saved.getSku());
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException cuando el SKU ya existe")
    void shouldThrowConflictExceptionWhenSkuExists()
    {
        // Arrange
        when(repository.existsBySku("P-100")).thenReturn(true);

        // Act & Assert
        ConflictException thrown = assertThrows(
                ConflictException.class,
                () -> productService.save(product)
        );

        assertEquals("El producto con SKU: P-100 ya existe", thrown.getMessage());
        verify(repository, never()).save(any(Product.class));
    }

    // FIND BY ID

    @Test
    @DisplayName("Debe retornar un producto cuando el ID existe")
    void shouldReturnProductWhenIdExists()
    {
        // Arrange
        when(repository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(product));

        // Act
        Product found = productService.findById(1L);

        // Assert
        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(repository, times(1)).findByIdAndActiveTrue(1L);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID no existe")
    void shouldThrowResourceNotFoundExceptionWhenIdNotFound()
    {
        // Arrange
        when(repository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findById(99L)
        );

        assertEquals("Producto no encontrado o inactivo: 99", thrown.getMessage());
        verify(repository, times(1)).findByIdAndActiveTrue(99L);
    }

    // DELETE

    @Test
    @DisplayName("Debe desactivar un producto correctamente (soft delete)")
    void shouldDeactivateProductSuccessfully()
    {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenReturn(product);

        // Act
        productService.deleteById(1L);

        // Assert
        assertFalse(product.getActive());
        verify(repository, times(1)).save(any(Product.class));
    }
}
