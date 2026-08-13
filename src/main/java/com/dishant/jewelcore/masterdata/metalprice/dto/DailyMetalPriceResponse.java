package com.dishant.jewelcore.masterdata.metalprice.dto;

import com.dishant.jewelcore.masterdata.metalprice.entity.DailyMetalPrice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyMetalPriceResponse(
        Long id,
        Long metalId,
        String metalName,
        String metalCode,
        LocalDate priceDate,
        BigDecimal pricePerGram,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static DailyMetalPriceResponse from(
            DailyMetalPrice price) {

        return new DailyMetalPriceResponse(
                price.getId(),
                price.getMetal().getId(),
                price.getMetal().getName(),
                price.getMetal().getCode(),
                price.getPriceDate(),
                price.getPricePerGram(),
                price.getCreatedAt(),
                price.getUpdatedAt()
        );
    }
}