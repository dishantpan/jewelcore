package com.dishant.jewelcore.inventory.stock.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record InventoryItemCreateRequest(

        @NotBlank(message = "Item code is required")
        @Size(max = 50)
        String itemCode,

        @NotNull(message = "Jewellery id is required")
        Long jewelleryId,

        @NotNull(message = "Gross weight is required")
        @DecimalMin(value = "0.001")
        BigDecimal grossWeight,

        @NotNull(message = "Stone weight is required")
        @DecimalMin(value = "0.000")
        BigDecimal stoneWeight,

        @NotNull(message = "Net weight is required")
        @DecimalMin(value = "0.001")
        BigDecimal netWeight,

        @NotNull(message = "Purchase cost is required")
        @DecimalMin(value = "0.00")
        BigDecimal purchaseCost,

        @Size(max = 100)
        String location
) {
}