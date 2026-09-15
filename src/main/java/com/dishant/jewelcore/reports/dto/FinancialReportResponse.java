package com.dishant.jewelcore.reports.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialReportResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal grossProfit;
    private BigDecimal totalGstCollected;
    private BigDecimal totalDiscounts;
    private BigDecimal netProfit;
    private List<GstSummary> gstBreakdown;
    private List<PaymentMethodSummary> paymentMethods;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GstSummary {
        private String gstRate;
        private BigDecimal taxableAmount;
        private BigDecimal gstAmount;
        private BigDecimal totalAmount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentMethodSummary {
        private String method;
        private BigDecimal totalAmount;
        private Long transactionCount;
    }
}