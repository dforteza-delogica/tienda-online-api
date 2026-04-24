package com.delogica.tienda_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.delogica.tienda_api.exception.ConflictException;
import com.delogica.tienda_api.exception.ResourceNotFoundException;
import com.delogica.tienda_api.repository.AddressRepository;
import com.delogica.tienda_api.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest
{
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private Address  address;

    @BeforeEach
    void setUp()
    {
        customer = Customer.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phone("+34 600 111 222")
                .build();

        address = Address.builder()
                .id(1L)
                .customer(customer)
                .line1("Calle Mayor 1")
                .city("Madrid")
                .postalCode("28013")
                .country("España")
                .isDefault(false)
                .build();
    }

    // SAVE 

    @Test
    @DisplayName("Debe guardar un cliente correctamente")
    void shouldSaveCustomerSuccessfully()
    {
        // Arrange
        when(customerRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        Customer saved = customerService.save(customer);

        // Assert
        assertNotNull(saved);
        assertEquals("john.doe@example.com", saved.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException cuando el email ya existe")
    void shouldThrowConflictExceptionWhenEmailExists()
    {
        // Arrange
        when(customerRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // Act & Assert
        ConflictException thrown = assertThrows(
                ConflictException.class,
                () -> customerService.save(customer)
        );

        assertEquals("Ya existe un cliente con el email: john.doe@example.com", thrown.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // UPDATE

    @Test
    @DisplayName("Debe lanzar ConflictException cuando el email está en uso por otro cliente")
    void shouldThrowConflictExceptionWhenEmailInUseByAnotherCustomer()
    {
        // Arrange
        customer.setEmail("nuevo@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(
                Customer.builder().id(1L).email("john.doe@example.com").build()));
        when(customerRepository.existsByEmail("nuevo@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(
                ConflictException.class,
                () -> customerService.update(customer)
        );

        verify(customerRepository, never()).save(any(Customer.class));
    }

    // ADD ADDRESS

    @Test
    @DisplayName("Debe añadir una dirección a un cliente correctamente")
    void shouldAddAddressToCustomerSuccessfully()
    {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        // Act
        Address saved = customerService.addAddress(1L, address);

        // Assert
        assertNotNull(saved);
        assertEquals(customer, saved.getCustomer());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el cliente no existe al añadir dirección")
    void shouldThrowExceptionWhenCustomerNotFoundOnAddAddress()
    {
        // Arrange
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.addAddress(99L, address)
        );

        verify(addressRepository, never()).save(any(Address.class));
    }
}
