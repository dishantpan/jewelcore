package com.dishant.jewelcore.inventory.jewellery.dto;

import com.dishant.jewelcore.inventory.jewellery.entity.Jewellery;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record JewelleryResponse(
        Long id,
        String sku,
        String name,
        String description,

        Long categoryId,
        String categoryName,
        String categoryCode,

        Long metalId,
        String metalName,
        String metalCode,

        Long purityId,
        String purityName,
        String purityCode,

        BigDecimal grossWeight,
        BigDecimal stoneWeight,
        BigDecimal netWeight,
        BigDecimal makingCharge,

        boolean active,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static JewelleryResponse from(Jewellery jewellery) {

        return new JewelleryResponse(
                jewellery.getId(),
                jewellery.getSku(),
                jewellery.getName(),
                jewellery.getDescription(),

                jewellery.getCategory().getId(),
                jewellery.getCategory().getName(),
                jewellery.getCategory().getCode(),

                jewellery.getMetal().getId(),
                jewellery.getMetal().getName(),
                jewellery.getMetal().getCode(),

                jewellery.getPurity().getId(),
                jewellery.getPurity().getName(),
                jewellery.getPurity().getCode(),

                jewellery.getGrossWeight(),
                jewellery.getStoneWeight(),
                jewellery.getNetWeight(),
                jewellery.getMakingCharge(),

                jewellery.isActive(),

                jewellery.getCreatedAt(),
                jewellery.getUpdatedAt()
        );
    }
}