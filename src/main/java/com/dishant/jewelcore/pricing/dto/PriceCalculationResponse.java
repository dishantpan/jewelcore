package com.dishant.jewelcore.pricing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PriceCalculationResponse(

        Long inventoryItemId,
        String itemCode,

        Long jewelleryId,
        String jewelleryName,

        String metalName,
        String metalCode,

        String purityName,
        String purityCode,
        BigDecimal purityPercentage,

        LocalDate priceDate,
        BigDecimal metalRatePerGram,

        BigDecimal netWeight,
        BigDecimal pureMetalRatePerGram,
        BigDecimal metalValue,

        BigDecimal makingCharge,
        BigDecimal finalPrice
) {
}