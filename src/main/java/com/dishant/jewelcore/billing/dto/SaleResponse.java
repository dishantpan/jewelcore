package com.dishant.jewelcore.billing.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponse {

    private Long id;
    private String saleNumber;
    private CustomerSummary customer;
    private LocalDateTime saleDate;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxableAmount;
    private BigDecimal gstRate;
    private BigDecimal gstAmount;
    private BigDecimal grandTotal;
    private String paymentStatus;
    private List<SaleItemResponse> items;
    private List<PaymentSummary> payments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerSummary {
        private Long id;
        private String name;
        private String phone;
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaleItemResponse {
        private Long id;
        private Long inventoryItemId;
        private String itemCode;
        private String jewelleryName;
        private String metalName;
        private String purityName;
        private BigDecimal purityPercentage;
        private BigDecimal grossWeight;
        private BigDecimal stoneWeight;
        private BigDecimal netWeight;
        private BigDecimal metalRatePerGram;
        private BigDecimal pureMetalRatePerGram;
        private BigDecimal metalValue;
        private BigDecimal makingCharge;
        private BigDecimal itemPrice;
        private BigDecimal gstRate;
        private BigDecimal gstAmount;
        private BigDecimal totalAmount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentSummary {
        private Long id;
        private String paymentMethod;
        private BigDecimal amount;
        private LocalDateTime paymentDate;
        private String referenceNumber;
    }
}