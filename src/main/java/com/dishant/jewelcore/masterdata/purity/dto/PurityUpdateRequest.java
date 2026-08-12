package com.dishant.jewelcore.masterdata.purity.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PurityUpdateRequest(

        @NotBlank(message = "Purity name is required")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Purity code is required")
        @Size(max = 20)
        String code,

        @NotNull(message = "Purity percentage is required")
        @DecimalMin(value = "0.01")
        @DecimalMax(value = "100.00")
        BigDecimal percentage
) {
}