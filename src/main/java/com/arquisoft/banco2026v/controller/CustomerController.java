package com.arquisoft.banco2026v.controller;

import com.arquisoft.banco2026v.dto.CustomerDTO;
import com.arquisoft.banco2026v.dto.PagedResponseDTO;
import com.arquisoft.banco2026v.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Validated
@RestController
@RequestMapping({"/api/customers", "/api/v1/customers"})
public class CustomerController {

    private final CustomerService customerFacade;

    public CustomerController(CustomerService customerFacade) {
        this.customerFacade = customerFacade;
    }

    @GetMapping
    public ResponseEntity<PagedResponseDTO<CustomerDTO>> getAllCustomers(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(PagedResponseDTO.from(customerFacade.getAllCustomers(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(customerFacade.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(
            @Valid @RequestBody CustomerDTO customerDTO,
            UriComponentsBuilder uriBuilder
    ) {
        CustomerDTO createdCustomer = customerFacade.createCustomer(customerDTO);
        URI location = uriBuilder.path("/api/v1/customers/{id}")
                .buildAndExpand(createdCustomer.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdCustomer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CustomerDTO customerDTO
    ) {
        return ResponseEntity.ok(customerFacade.updateCustomer(id, customerDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable @Positive Long id) {
        customerFacade.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
