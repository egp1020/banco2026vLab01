package com.arquisoft.banco2026v.dto;

public record PaginationMetaDTO(
        int page,
        int size,
        long totalElements,
        int totalPages,
        int numberOfElements,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
) {
}
