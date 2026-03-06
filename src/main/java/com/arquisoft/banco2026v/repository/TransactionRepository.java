package com.arquisoft.banco2026v.repository;

import com.arquisoft.banco2026v.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findBySenderAccountNumberOrReceiverAccountNumber(
            String senderAccountNumber,
            String receiverAccountNumber,
            Pageable pageable
    );

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
}
