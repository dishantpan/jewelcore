package com.dishant.jewelcore.masterdata.metalprice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyMetalPriceCreateRequest(

        @NotNull(message = "Metal id is required")
        Long metalId,

        @NotNull(message = "Price date is required")
        LocalDate priceDate,

        @NotNull(message = "Price per gram is required")
        @DecimalMin(value = "0.01")
        BigDecimal pricePerGram
) {
}