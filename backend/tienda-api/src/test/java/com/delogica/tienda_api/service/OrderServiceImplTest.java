package com.delogica.tienda_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.delogica.tienda_api.domain.Address;
import com.delogica.tienda_api.domain.Customer;
import com.delogica.tienda_api.domain.Order;
import com.delogica.tienda_api.domain.OrderItem;
import com.delogica.tienda_api.domain.OrderStatus;
import com.delogica.tienda_api.domain.Product;
import com.delogica.tienda_api.dto.request.OrderItemRequestDto;
import com.delogica.tienda_api.dto.request.OrderRequestDto;
import com.delogica.tienda_api.exception.ConflictException;
import com.delogica.tienda_api.exception.InvalidOperationException;
import com.delogica.tienda_api.exception.ResourceNotFoundException;
import com.delogica.tienda_api.mapper.OrderMapper;
import com.delogica.tienda_api.repository.AddressRepository;
import com.delogica.tienda_api.repository.CustomerRepository;
import com.delogica.tienda_api.repository.OrderRepository;
import com.delogica.tienda_api.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest
{
    @Mock
    private OrderRepository     orderRepository;
    @Mock
    private CustomerRepository  customerRepository;
    @Mock
    private AddressRepository   addressRepository;
    @Mock
    private ProductRepository   productRepository;
    @Mock
    private OrderMapper         orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer        customer;
    private Address         address;
    private Product         product;
    private Order           order;
    private OrderRequestDto requestDto;

    @BeforeEach
    void setUp()
    {
        customer = Customer.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .build();

        address = Address.builder()
                .id(1L)
                .customer(customer)
                .line1("Calle Mayor 1")
                .city("Madrid")
                .postalCode("28013")
                .country("España")
                .isDefault(true)
                .build();

        product = Product.builder()
                .id(1L)
                .sku("P-100")
                .name("Café Premium 250g")
                .price(new BigDecimal("7.50"))
                .stock(100)
                .active(true)
                .build();

        order = Order.builder()
                .id(1L)
                .customer(customer)
                .shippingAddress(address)
                .status(OrderStatus.CREATED)
                .total(new BigDecimal("15.00"))
                .items(new ArrayList<>())
                .build();

        OrderItemRequestDto itemDto = new OrderItemRequestDto();
        itemDto.setProductId(1L);
        itemDto.setQuantity(2);

        requestDto = new OrderRequestDto();
        requestDto.setCustomerId(1L);
        requestDto.setAddressId(1L);
        requestDto.setItems(List.of(itemDto));
    }

    // SAVE

    @Test
    @DisplayName("Debe crear un pedido con el total calculado correctamente")
    void shouldCreateOrderWithCorrectTotal()
    {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation ->
        {
            Order saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        Order saved = orderService.save(requestDto);

        // Assert
        assertNotNull(saved);
        assertEquals(new BigDecimal("15.00"), saved.getTotal());
        assertEquals(1, saved.getItems().size());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el cliente no existe")
    void shouldThrowExceptionWhenCustomerNotFound()
    {
        // Arrange
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        requestDto.setCustomerId(99L);

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.save(requestDto)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar InvalidOperationException cuando la dirección no pertenece al cliente")
    void shouldThrowExceptionWhenAddressNotBelongsToCustomer()
    {
        // Arrange
        Customer otherCustomer = Customer.builder().id(2L).build();
        Address otherAddress = Address.builder()
                .id(3L)
                .customer(otherCustomer)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressRepository.findById(3L)).thenReturn(Optional.of(otherAddress));
        requestDto.setAddressId(3L);

        // Act & Assert
        assertThrows(
                InvalidOperationException.class,
                () -> orderService.save(requestDto)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException cuando el stock es insuficiente")
    void shouldThrowExceptionWhenInsufficientStock()
    {
        // Arrange
        product.setStock(1);
        requestDto.getItems().get(0).setQuantity(99);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(
                ConflictException.class,
                () -> orderService.save(requestDto)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    // UPDATE STATUS 

    @Test
    @DisplayName("Debe cambiar el estado de CREATED a PAID correctamente")
    void shouldUpdateStatusFromCreatedToPaid()
    {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order updated = orderService.updateStatus(1L, OrderStatus.PAID);

        // Assert
        assertEquals(OrderStatus.PAID, updated.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar InvalidOperationException en transición de estado inválida")
    void shouldThrowExceptionWhenInvalidStatusTransition()
    {
        // Arrange
        order.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        InvalidOperationException thrown = assertThrows(
                InvalidOperationException.class,
                () -> orderService.updateStatus(1L, OrderStatus.CREATED)
        );

        assertEquals("Transición de estado no permitida: SHIPPED → CREATED", thrown.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe devolver stock al cancelar un pedido")
    void shouldRestoreStockWhenOrderCancelled()
    {
        // Arrange
        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("7.50"))
                .build();
        order.getItems().add(item);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderService.updateStatus(1L, OrderStatus.CANCELLED);

        // Assert
        assertEquals(102, product.getStock());
        verify(productRepository, times(1)).save(product);
    }
}
