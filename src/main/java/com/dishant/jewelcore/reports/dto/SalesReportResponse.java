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
public class SalesReportResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalSales;
    private BigDecimal totalDiscount;
    private BigDecimal totalGst;
    private BigDecimal totalNetSales;
    private Long totalTransactions;
    private Long totalItemsSold;
    private List<DailySalesSummary> dailySales;
    private List<CategorySalesSummary> byCategory;
    private List<MetalSalesSummary> byMetal;
    private List<SalespersonSummary> bySalesperson;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailySalesSummary {
        private LocalDate date;
        private BigDecimal totalSales;
        private BigDecimal discount;
        private BigDecimal gst;
        private BigDecimal netSales;
        private Long transactionCount;
        private Long itemsSold;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategorySalesSummary {
        private String category;
        private BigDecimal totalSales;
        private Long itemsSold;
        private Long transactionCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MetalSalesSummary {
        private String metal;
        private BigDecimal totalSales;
        private Long itemsSold;
        private Long transactionCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalespersonSummary {
        private String username;
        private String fullName;
        private BigDecimal totalSales;
        private Long transactionCount;
        private Long itemsSold;
    }
}