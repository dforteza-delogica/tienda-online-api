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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("Creating order for customer: {}, items: {}", dto.getCustomerId(), dto.getItems().size());

        Customer customer = customerRepository
                .findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + dto.getCustomerId()));

        Address address = addressRepository
                .findById(dto.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + dto.getAddressId()));

        if (!address.getCustomer().getId().equals(customer.getId()))
            throw new InvalidOperationException("Address " + dto.getAddressId() + " does not belong to customer " + dto.getCustomerId());

        Order order = new Order();
        order.setCustomer(customer);
        order.setShippingAddress(address);
        order.setStatus(OrderStatus.CREATED);
        order.setOrderDate(LocalDateTime.now());
        order.setTotal(BigDecimal.ZERO);
        
        for (OrderItemRequestDto itemDto : dto.getItems())
        {
            Product product = productRepository
                    .findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemDto.getProductId()));

            if (!product.getActive())
                throw new InvalidOperationException("Product is not active: " + product.getId());

            if (product.getStock() < itemDto.getQuantity()) 
            {
                log.error("Insufficient stock - product: {}, requested: {}, available: {}", 
                    product.getId(), itemDto.getQuantity(), product.getStock());
                throw new ConflictException("Insufficient stock for product " + product.getName() + " | Available: " + product.getStock());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            
            BigDecimal unitPrice = product.getPrice();
            item.setUnitPrice(unitPrice);
                                        
            order.getItems().add(item);

            product.setStock(product.getStock() - itemDto.getQuantity());

            log.debug("Stock deducted - product: {}, quantity: {}, remaining stock: {}", 
                product.getId(), itemDto.getQuantity(), product.getStock());
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem i : order.getItems())
        {
            BigDecimal lineTotal = i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()));
            total = total.add(lineTotal);
        }
        order.setTotal(total);

        Order saved = orderRepository.save(order);
        log.info("Order created successfully - id: {}, total: {}, items: {}", 
            saved.getId(), saved.getTotal(), saved.getItems().size());
        
        return (saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Order findById(Long id)
    {
        log.debug("Finding order by id: {}", id);
        
        return (orderRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> findAll(Long customerId, OrderStatus status, Pageable pageable)
    {
        log.debug("Listing orders - customer: {}, status: {}, page: {}",
            customerId, status, pageable.getPageNumber());

        if (customerId != null && !customerRepository.existsById(customerId))
            throw new ResourceNotFoundException("Customer not found: " + customerId);

        Page<Order> page = orderRepository.findByFilters(customerId, status, pageable);

        List<OrderResponseDto> dtos = page.getContent()
                .stream()
                .map(orderMapper::toResponseDto)
                .toList();
        
        return (new PageImpl<>(dtos, pageable, page.getTotalElements()));
    }

    @Override
    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus)
    {
        log.info("Updating order status - id: {}, new status: {}", id, newStatus);

        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        OrderStatus current = order.getStatus();

        if (!(
                (current == OrderStatus.CREATED && newStatus == OrderStatus.PAID)
                || (current == OrderStatus.CREATED && newStatus == OrderStatus.CANCELLED)
                || (current == OrderStatus.PAID    && newStatus == OrderStatus.SHIPPED)
                || (current == OrderStatus.PAID    && newStatus == OrderStatus.CANCELLED))
        ) 
        {
            log.error("Invalid status transition - order: {}, current status: {}, requested status: {}", 
                id, current, newStatus);
            throw new InvalidOperationException("Status transition not allowed: " + current + " -> " + newStatus);
        }
        
        if (newStatus == OrderStatus.CANCELLED)
        {
            log.info("Cancelling order - restoring stock for {} items", order.getItems().size());
            for (OrderItem item : order.getItems())
            {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(newStatus);

        Order updated = orderRepository.save(order);
        log.info("Order status updated successfully - id: {}, status: {}", updated.getId(), updated.getStatus());
        
        return (updated);
    }
}