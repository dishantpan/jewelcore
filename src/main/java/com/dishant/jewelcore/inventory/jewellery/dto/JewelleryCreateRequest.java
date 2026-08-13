package com.dishant.jewelcore.inventory.jewellery.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record JewelleryCreateRequest(

        @NotBlank(message = "SKU is required")
        @Size(max = 50)
        String sku,

        @NotBlank(message = "Jewellery name is required")
        @Size(max = 150)
        String name,

        @Size(max = 500)
        String description,

        @NotNull(message = "Category id is required")
        Long categoryId,

        @NotNull(message = "Metal id is required")
        Long metalId,

        @NotNull(message = "Purity id is required")
        Long purityId,

        @NotNull(message = "Gross weight is required")
        @DecimalMin(
                value = "0.001",
                message = "Gross weight must be greater than zero"
        )
        BigDecimal grossWeight,

        @NotNull(message = "Stone weight is required")
        @DecimalMin(
                value = "0.000",
                message = "Stone weight cannot be negative"
        )
        BigDecimal stoneWeight,

        @NotNull(message = "Net weight is required")
        @DecimalMin(
                value = "0.001",
                message = "Net weight must be greater than zero"
        )
        BigDecimal netWeight,

        @NotNull(message = "Making charge is required")
        @DecimalMin(
                value = "0.00",
                message = "Making charge cannot be negative"
        )
        BigDecimal makingCharge
) {
}