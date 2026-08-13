package com.dishant.jewelcore.pricing.service;

import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItem;
import com.dishant.jewelcore.inventory.stock.repository.InventoryItemRepository;
import com.dishant.jewelcore.masterdata.metalprice.entity.DailyMetalPrice;
import com.dishant.jewelcore.masterdata.metalprice.repository.DailyMetalPriceRepository;
import com.dishant.jewelcore.pricing.dto.PriceCalculationRequest;
import com.dishant.jewelcore.pricing.dto.PriceCalculationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional(readOnly = true)
public class PricingService {

    private final InventoryItemRepository inventoryItemRepository;
    private final DailyMetalPriceRepository dailyMetalPriceRepository;

    public PricingService(
            InventoryItemRepository inventoryItemRepository,
            DailyMetalPriceRepository dailyMetalPriceRepository) {

        this.inventoryItemRepository = inventoryItemRepository;
        this.dailyMetalPriceRepository = dailyMetalPriceRepository;
    }

    public PriceCalculationResponse calculatePrice(
            PriceCalculationRequest request) {

        InventoryItem inventoryItem =
                inventoryItemRepository.findById(request.inventoryItemId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Inventory item not found with id: "
                                        + request.inventoryItemId()
                        ));

        DailyMetalPrice dailyMetalPrice =
                dailyMetalPriceRepository
                        .findByMetalIdAndPriceDate(
                                inventoryItem.getJewellery()
                                        .getMetal()
                                        .getId(),
                                request.priceDate()
                        )
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Metal price not found for metal "
                                        + inventoryItem.getJewellery()
                                        .getMetal()
                                        .getId()
                                        + " on "
                                        + request.priceDate()
                        ));

        BigDecimal purityPercentage =
                inventoryItem.getJewellery()
                        .getPurity()
                        .getPercentage();

        BigDecimal purityFactor =
                purityPercentage
                        .divide(
                                BigDecimal.valueOf(100),
                                6,
                                RoundingMode.HALF_UP
                        );

        BigDecimal pureMetalRatePerGram =
                dailyMetalPrice
                        .getPricePerGram()
                        .multiply(purityFactor)
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal metalValue =
                inventoryItem
                        .getNetWeight()
                        .multiply(pureMetalRatePerGram)
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal makingCharge =
                inventoryItem
                        .getJewellery()
                        .getMakingCharge()
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal finalPrice =
                metalValue
                        .add(makingCharge)
                        .setScale(2, RoundingMode.HALF_UP);

        return new PriceCalculationResponse(
                inventoryItem.getId(),
                inventoryItem.getItemCode(),

                inventoryItem.getJewellery().getId(),
                inventoryItem.getJewellery().getName(),

                inventoryItem.getJewellery().getMetal().getName(),
                inventoryItem.getJewellery().getMetal().getCode(),

                inventoryItem.getJewellery().getPurity().getName(),
                inventoryItem.getJewellery().getPurity().getCode(),
                purityPercentage,

                request.priceDate(),
                dailyMetalPrice.getPricePerGram(),

                inventoryItem.getNetWeight(),
                pureMetalRatePerGram,
                metalValue,

                makingCharge,
                finalPrice
        );
    }
}