package com.arquisoft.banco2026v.service;

import com.arquisoft.banco2026v.dto.CustomerDTO;
import com.arquisoft.banco2026v.entity.Customer;
import com.arquisoft.banco2026v.mapper.CustomerMapper;
import com.arquisoft.banco2026v.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void deleteCustomerShouldFailWhenBalanceIsPositive() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setBalance(new BigDecimal("10.00"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(IllegalStateException.class, () -> customerService.deleteCustomer(1L));
        verify(customerRepository, never()).delete(customer);
    }

    @Test
    void createCustomerShouldFailWhenAccountNumberAlreadyExists() {
        CustomerDTO dto = new CustomerDTO();
        dto.setAccountNumber("12345678");
        dto.setFirstName("Ana");
        dto.setLastName("Lopez");
        dto.setBalance(BigDecimal.ZERO);

        Customer existing = new Customer();
        existing.setId(2L);
        existing.setAccountNumber("12345678");

        when(customerRepository.findByAccountNumber("12345678")).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(dto));
        verify(customerRepository, never()).save(org.mockito.ArgumentMatchers.any(Customer.class));
    }
}
