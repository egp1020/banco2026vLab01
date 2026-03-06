package com.arquisoft.banco2026v.controller;

import com.arquisoft.banco2026v.dto.TransactionDTO;
import com.arquisoft.banco2026v.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Validated
@RestController
@RequestMapping(value = {"/api/transactions", "/api/v1/transactions"}, produces = "application/json")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    @PostMapping
    public ResponseEntity<TransactionDTO> transferMoney(
            @Valid @RequestBody TransactionDTO transactionDTO,
            UriComponentsBuilder uriBuilder
    ) {
        TransactionDTO createdTransaction = transactionService.transferMoney(transactionDTO);
        URI location = uriBuilder.path("/api/v1/transactions/id/{id}")
                .buildAndExpand(createdTransaction.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTransaction);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping({"/account/{accountNumber}", "/{accountNumber}"})
    public ResponseEntity<Page<TransactionDTO>> getTransactionsByAccount(
            @PathVariable @NotBlank String accountNumber,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(transactionService.getTransactionsForAccount(accountNumber, pageable));
    }
}
