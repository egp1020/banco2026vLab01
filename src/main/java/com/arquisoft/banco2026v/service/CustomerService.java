package com.arquisoft.banco2026v.service;

import com.arquisoft.banco2026v.dto.CustomerDTO;
import com.arquisoft.banco2026v.entity.Customer;
import com.arquisoft.banco2026v.exception.NotFoundException;
import com.arquisoft.banco2026v.mapper.CustomerMapper;
import com.arquisoft.banco2026v.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(customerMapper::toDTO);
    }

    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id).map(customerMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        validateAccountNumberUnique(customerDTO.getAccountNumber(), null);

        Customer customer = customerMapper.toEntity(customerDTO);
        return customerMapper.toDTO(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        validateAccountNumberUnique(customerDTO.getAccountNumber(), id);

        customer.setAccountNumber(customerDTO.getAccountNumber());
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setBalance(customerDTO.getBalance());

        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        if (customer.getBalance() != null && customer.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Cannot delete customer with positive balance");
        }

        customerRepository.delete(customer);
    }

    private void validateAccountNumberUnique(String accountNumber, Long currentCustomerId) {
        customerRepository.findByAccountNumber(accountNumber).ifPresent(existing -> {
            if (currentCustomerId == null || !existing.getId().equals(currentCustomerId)) {
                throw new IllegalArgumentException("Account number already exists");
            }
        });
    }
}
