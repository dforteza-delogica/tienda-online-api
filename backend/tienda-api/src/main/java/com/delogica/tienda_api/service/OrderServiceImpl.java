package com.delogica.tienda_api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delogica.tienda_api.domain.Address;
import com.delogica.tienda_api.domain.Customer;
import com.delogica.tienda_api.domain.Order;
import com.delogica.tienda_api.domain.OrderItem;
import com.delogica.tienda_api.domain.OrderStatus;
import com.delogica.tienda_api.domain.Product;
import com.delogica.tienda_api.dto.request.OrderItemRequestDto;
import com.delogica.tienda_api.dto.request.OrderRequestDto;
import com.delogica.tienda_api.dto.response.OrderResponseDto;
import com.delogica.tienda_api.exception.ConflictException;
import com.delogica.tienda_api.exception.InvalidOperationException;
import com.delogica.tienda_api.exception.ResourceNotFoundException;
import com.delogica.tienda_api.mapper.OrderMapper;
import com.delogica.tienda_api.repository.AddressRepository;
import com.delogica.tienda_api.repository.CustomerRepository;
import com.delogica.tienda_api.repository.OrderRepository;
import com.delogica.tienda_api.repository.ProductRepository;
import com.delogica.tienda_api.service.interfaces.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService
{
    private final OrderRepository       orderRepository;
    private final CustomerRepository    customerRepository;
    private final AddressRepository     addressRepository;
    private final ProductRepository     productRepository;

    private final OrderMapper           orderMapper;

    @Override
    @Transactional
    public Order save(OrderRequestDto dto)
    {
        // 1. VALIDAR QUE EL CLIENTE EXISTE
        Customer customer = customerRepository
                                .findById(dto.getCustomerId())
                                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + dto.getCustomerId()));

        // 2. VALIDAR QUE LA DIRECCION EXISTE
        Address address = addressRepository
                                .findById(dto.getAddressId())
                                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada: " + dto.getAddressId()));

        // 3. VALIDAR QUE LA DIRECCION PERTENECE AL CLIENTE
        if (!address.getCustomer().getId().equals(customer.getId()))
            throw new InvalidOperationException("La dirección " + dto.getAddressId() + " no pertenece al cliente " + dto.getCustomerId());

        // 4. CREAR EL PEDIDO BASE
        Order order = new Order();
        order.setCustomer(customer);
        order.setShippingAddress(address);
        order.setStatus(OrderStatus.CREATED);
        order.setOrderDate(LocalDateTime.now());
        order.setTotal(BigDecimal.ZERO);

        // 5. PROCESAR CADA ITEM DEL DTO
        for (OrderItemRequestDto itemDto : dto.getItems())
        {
            // 5.1 VALIDAR QUE EL PRODUCTO EXISTE
            Product product = productRepository
                                    .findById(itemDto.getProductId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + itemDto.getProductId()));

            // 5.2 VALIDAR QUE EL PRODUCTO ESTA ACTIVO
            if (!product.getActive())
                throw new InvalidOperationException("El producto no está activo: " + product.getId());

            // 5.3 VALIDAR STOCK SUFICIENTE
            if (product.getStock() < itemDto.getQuantity())
                throw new ConflictException("Stock insuficiente en el producto "+product.getName() +" | Disponible:" + product.getStock());

            // 5.4 CAPTURAR PRECIO SNAPSHOT
            BigDecimal unitPrice = product.getPrice();

            // 5.5 CREAR EL ORDER ITEM
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(unitPrice);
                                        
            // 5.6 AÑADIR EL ITEM AL PEDIDO
            order.getItems().add(item);

            // DESCONTAR STOCK
            product.setStock(product.getStock() - itemDto.getQuantity());
        }

        // 6. CALCULAR TOTAL DEL PEDIDO
        BigDecimal total = order.getItems()
                                .stream()
                                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);

        // 7. PERSISTIR Y DEVOLVER
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order findById(Long id)
    {
        // 1. BUSCAR O LANZAR EXCEPCION
        return orderRepository
                    .findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> findAll(Long customerId, OrderStatus status, Pageable pageable)
    {
        // 1. OBTENER PAGE CON FILTROS
        Page<Order> page = orderRepository.finByFilters(customerId, status, pageable);

        // 2. CONVERTIR A DTO
        List<OrderResponseDto> dtos = page.getContent()
                                        .stream()
                                        .map(order -> orderMapper.toResponseDto(order))
                                        .toList();
        return (new PageImpl<>(dtos, pageable, page.getTotalElements()));
    }

    @Override
    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus)
    {
        // 1. BUSCAR EL PEDIDO O LANZAR EXCEPCION
        Order order = orderRepository
                            .findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id));

        // 2. VALIDAR LA TRANSICION DE ESTADO
        //    Transiciones válidas:
        //    CREATED → PAID
        //    CREATED → CANCELLED
        //    PAID    → SHIPPED
        //    PAID    → CANCELLED
        OrderStatus current = order.getStatus();
        boolean valid = (current == OrderStatus.CREATED && newStatus == OrderStatus.PAID)
                     || (current == OrderStatus.CREATED && newStatus == OrderStatus.CANCELLED)
                     || (current == OrderStatus.PAID    && newStatus == OrderStatus.SHIPPED)
                     || (current == OrderStatus.PAID    && newStatus == OrderStatus.CANCELLED);

        if (!valid)
            throw new InvalidOperationException(
                "Transición de estado no permitida: " + current + " → " + newStatus);

        
        // 3. SI SE CANCELA DESHACER EL STOCK
        if (newStatus == OrderStatus.CANCELLED)
        {
            for (OrderItem item : order.getItems())
            {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        // 3. APLICAR NUEVO ESTADO
        order.setStatus(newStatus);

        // 4. PERSISTIR Y DEVOLVER
        return orderRepository.save(order);
    }
}


