package com.dishant.jewelcore.inventory.stock.dto;

import com.dishant.jewelcore.inventory.stock.entity.InventoryItem;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryItemResponse(

        Long id,
        String itemCode,

        Long jewelleryId,
        String jewellerySku,
        String jewelleryName,

        BigDecimal grossWeight,
        BigDecimal stoneWeight,
        BigDecimal netWeight,

        BigDecimal purchaseCost,

        InventoryItemStatus status,

        String location,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static InventoryItemResponse from(
            InventoryItem item) {

        return new InventoryItemResponse(
                item.getId(),
                item.getItemCode(),

                item.getJewellery().getId(),
                item.getJewellery().getSku(),
                item.getJewellery().getName(),

                item.getGrossWeight(),
                item.getStoneWeight(),
                item.getNetWeight(),

                item.getPurchaseCost(),

                item.getStatus(),

                item.getLocation(),

                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}