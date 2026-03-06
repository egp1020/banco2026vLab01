package com.arquisoft.banco2026v.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponseDTO<T>(
        List<T> data,
        PaginationMetaDTO meta
) {
    public static <T> PagedResponseDTO<T> from(Page<T> page) {
        PaginationMetaDTO meta = new PaginationMetaDTO(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
        return new PagedResponseDTO<>(page.getContent(), meta);
    }
}
