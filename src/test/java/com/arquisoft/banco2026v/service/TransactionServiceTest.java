package com.arquisoft.banco2026v.service;

import com.arquisoft.banco2026v.dto.TransactionDTO;
import com.arquisoft.banco2026v.entity.Customer;
import com.arquisoft.banco2026v.entity.Transaction;
import com.arquisoft.banco2026v.mapper.TransferMapper;
import com.arquisoft.banco2026v.repository.CustomerRepository;
import com.arquisoft.banco2026v.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransferMapper transferMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void transferShouldFailWhenSenderAndReceiverAreEqual() {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderAccountNumber("123456");
        dto.setReceiverAccountNumber("123456");
        dto.setAmount(new BigDecimal("5.00"));

        assertThrows(IllegalArgumentException.class, () -> transactionService.transferMoney(dto));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void transferShouldReturnExistingTransactionWhenIdempotencyKeyExists() {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderAccountNumber("111111");
        dto.setReceiverAccountNumber("222222");
        dto.setAmount(new BigDecimal("5.00"));
        dto.setIdempotencyKey("idem-1");

        Transaction existing = new Transaction();
        existing.setId(99L);

        TransactionDTO mapped = new TransactionDTO();
        mapped.setId(99L);

        when(transactionRepository.findByIdempotencyKey("idem-1")).thenReturn(Optional.of(existing));
        when(transferMapper.toDTO(existing)).thenReturn(mapped);

        TransactionDTO result = transactionService.transferMoney(dto);

        assertEquals(99L, result.getId());
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
