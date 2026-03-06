package com.arquisoft.banco2026v.service;

import com.arquisoft.banco2026v.dto.TransactionDTO;
import com.arquisoft.banco2026v.entity.Customer;
import com.arquisoft.banco2026v.entity.Transaction;
import com.arquisoft.banco2026v.exception.NotFoundException;
import com.arquisoft.banco2026v.mapper.TransferMapper;
import com.arquisoft.banco2026v.repository.CustomerRepository;
import com.arquisoft.banco2026v.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final TransferMapper transferMapper;

    public TransactionService(
            TransactionRepository transactionRepository,
            CustomerRepository customerRepository,
            TransferMapper transferMapper
    ) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.transferMapper = transferMapper;
    }

    @Transactional
    public TransactionDTO transferMoney(TransactionDTO transactionDTO) {
        if (transactionDTO.getSenderAccountNumber().equals(transactionDTO.getReceiverAccountNumber())) {
            throw new IllegalArgumentException("Sender and receiver accounts must be different");
        }

        String normalizedIdempotencyKey = normalizeKey(transactionDTO.getIdempotencyKey());
        if (normalizedIdempotencyKey != null) {
            Transaction existing = transactionRepository.findByIdempotencyKey(normalizedIdempotencyKey).orElse(null);
            if (existing != null) {
                return transferMapper.toDTO(existing);
            }
        }

        Customer sender = customerRepository.findByAccountNumber(transactionDTO.getSenderAccountNumber())
                .orElseThrow(() -> new NotFoundException("Sender account does not exist"));
        Customer receiver = customerRepository.findByAccountNumber(transactionDTO.getReceiverAccountNumber())
                .orElseThrow(() -> new NotFoundException("Receiver account does not exist"));

        BigDecimal amount = transactionDTO.getAmount();
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient sender balance");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        customerRepository.save(sender);
        customerRepository.save(receiver);

        Transaction transaction = transferMapper.toEntity(transactionDTO);
        transaction.setId(null);
        transaction.setSenderAccountNumber(sender.getAccountNumber());
        transaction.setReceiverAccountNumber(receiver.getAccountNumber());
        transaction.setIdempotencyKey(normalizedIdempotencyKey);
        transaction.setTimestamp(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);

        return transferMapper.toDTO(transaction);
    }

    public Page<TransactionDTO> getTransactionsForAccount(String accountNumber, Pageable pageable) {
        return transactionRepository
                .findBySenderAccountNumberOrReceiverAccountNumber(accountNumber, accountNumber, pageable)
                .map(transferMapper::toDTO);
    }

    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));
        return transferMapper.toDTO(transaction);
    }

    private String normalizeKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            return null;
        }
        return idempotencyKey.trim();
    }
}
