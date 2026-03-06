package com.arquisoft.banco2026v.mapper;

import com.arquisoft.banco2026v.dto.CustomerDTO;
import com.arquisoft.banco2026v.entity.Customer;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);
    Customer toEntity(CustomerDTO customerDTO);
}
