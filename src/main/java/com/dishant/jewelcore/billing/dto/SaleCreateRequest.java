package com.dishant.jewelcore.billing.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleCreateRequest {

    private Long customerId;
    private List<SaleItemRequest> items;
    private BigDecimal discountAmount;
    private String notes;
    private List<PaymentRequest> payments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaleItemRequest {
        private Long inventoryItemId;
        private BigDecimal discountAmount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentRequest {
        private String paymentMethod;
        private BigDecimal amount;
        private String referenceNumber;
        private String notes;
    }
}