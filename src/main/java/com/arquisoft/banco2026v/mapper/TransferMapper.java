package com.arquisoft.banco2026v.mapper;

import com.arquisoft.banco2026v.dto.TransactionDTO;
import com.arquisoft.banco2026v.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferMapper {
    TransactionDTO toDTO(Transaction transaction);
    Transaction toEntity(TransactionDTO transactionDTO);
}
