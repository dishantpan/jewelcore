package com.dishant.jewelcore.billing.service;

import com.dishant.jewelcore.billing.dto.GstConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class GstCalculationService {

    private final GstConfig gstConfig;

    public BigDecimal calculateGstAmount(BigDecimal taxableAmount) {
        BigDecimal rate = gstConfig.getGstRate();
        return taxableAmount.multiply(rate)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTaxableAmount(BigDecimal subtotal, BigDecimal discountAmount) {
        return subtotal.subtract(discountAmount);
    }

    public BigDecimal calculateGrandTotal(BigDecimal taxableAmount, BigDecimal gstAmount) {
        return taxableAmount.add(gstAmount);
    }

    public BigDecimal calculateItemGst(BigDecimal itemPrice) {
        return itemPrice.multiply(gstConfig.getGstRate())
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }
}