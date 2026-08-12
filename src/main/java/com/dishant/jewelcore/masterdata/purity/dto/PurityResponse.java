package com.dishant.jewelcore.masterdata.purity.dto;

import com.dishant.jewelcore.masterdata.purity.entity.Purity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurityResponse(
        Long id,
        String name,
        String code,
        BigDecimal percentage,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PurityResponse from(Purity purity) {

        return new PurityResponse(
                purity.getId(),
                purity.getName(),
                purity.getCode(),
                purity.getPercentage(),
                purity.isActive(),
                purity.getCreatedAt(),
                purity.getUpdatedAt()
        );
    }
}