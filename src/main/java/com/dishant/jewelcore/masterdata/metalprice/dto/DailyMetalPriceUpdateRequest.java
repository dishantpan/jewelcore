package com.dishant.jewelcore.masterdata.metalprice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DailyMetalPriceUpdateRequest(

        @NotNull(message = "Price per gram is required")
        @DecimalMin(value = "0.01")
        BigDecimal pricePerGram
) {
}