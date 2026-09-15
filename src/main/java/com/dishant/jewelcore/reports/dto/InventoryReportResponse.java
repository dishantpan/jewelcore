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
public class InventoryReportResponse {

    private LocalDate asOfDate;
    private BigDecimal totalInventoryValue;
    private Long totalItems;
    private Long availableItems;
    private Long reservedItems;
    private Long soldItems;
    private Long damagedItems;
    private List<CategoryInventorySummary> byCategory;
    private List<MetalInventorySummary> byMetal;
    private List<AgingSummary> aging;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryInventorySummary {
        private String category;
        private Long totalItems;
        private Long availableItems;
        private BigDecimal totalValue;
        private BigDecimal averageValue;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MetalInventorySummary {
        private String metal;
        private Long totalItems;
        private Long availableItems;
        private BigDecimal totalWeight;
        private BigDecimal totalValue;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgingSummary {
        private String range;
        private Long itemCount;
        private BigDecimal totalValue;
    }
}