package com.dishant.jewelcore.pricing.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PriceCalculationRequest(

        @NotNull(message = "Inventory item id is required")
        Long inventoryItemId,

        @NotNull(message = "Price date is required")
        LocalDate priceDate
) {
}