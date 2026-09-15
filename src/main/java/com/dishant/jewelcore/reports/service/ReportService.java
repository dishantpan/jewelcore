package com.dishant.jewelcore.reports.service;

import com.dishant.jewelcore.billing.entity.Payment;
import com.dishant.jewelcore.billing.entity.Sale;
import com.dishant.jewelcore.billing.entity.SaleItem;
import com.dishant.jewelcore.billing.repository.SaleRepository;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItem;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItemStatus;
import com.dishant.jewelcore.inventory.stock.repository.InventoryItemRepository;
import com.dishant.jewelcore.reports.dto.FinancialReportResponse;
import com.dishant.jewelcore.reports.dto.InventoryReportResponse;
import com.dishant.jewelcore.reports.dto.SalesReportResponse;
import com.dishant.jewelcore.inventory.jewellery.entity.Jewellery;
import com.dishant.jewelcore.inventory.jewellery.repository.JewelleryRepository;
import com.dishant.jewelcore.user.entity.User;
import com.dishant.jewelcore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SaleRepository saleRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final JewelleryRepository jewelleryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public SalesReportResponse getSalesReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Sale> sales = saleRepository.findBySaleDateBetween(start, end);

        BigDecimal totalSales = sales.stream()
                .map(Sale::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = sales.stream()
                .map(Sale::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGst = sales.stream()
                .map(Sale::getGstAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalNetSales = sales.stream()
                .map(Sale::getTaxableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalTransactions = (long) sales.size();
        Long totalItemsSold = sales.stream()
                .flatMap(sale -> sale.getItems().stream())
                .count();

        // Daily sales
        Map<LocalDate, List<Sale>> salesByDate = sales.stream()
                .collect(Collectors.groupingBy(s -> s.getSaleDate().toLocalDate()));

        List<SalesReportResponse.DailySalesSummary> dailySales = salesByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<Sale> daySales = entry.getValue();
                    BigDecimal dayTotal = daySales.stream().map(Sale::getGrandTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal dayDiscount = daySales.stream().map(Sale::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal dayGst = daySales.stream().map(Sale::getGstAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal dayNet = daySales.stream().map(Sale::getTaxableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    Long dayTxCount = (long) daySales.size();
                    Long dayItems = daySales.stream().flatMap(s -> s.getItems().stream()).count();

                    return SalesReportResponse.DailySalesSummary.builder()
                            .date(entry.getKey())
                            .totalSales(dayTotal)
                            .discount(dayDiscount)
                            .gst(dayGst)
                            .netSales(dayNet)
                            .transactionCount(dayTxCount)
                            .itemsSold(dayItems)
                            .build();
                })
                .collect(Collectors.toList());

        // By category
        Map<String, List<SaleItem>> salesByCategory = sales.stream()
                .flatMap(sale -> sale.getItems().stream())
                .collect(Collectors.groupingBy(item -> {
                    String cat = item.getJewelleryName();
                    return cat != null ? cat : "Unknown";
                }));

        List<SalesReportResponse.CategorySalesSummary> byCategory = salesByCategory.entrySet().stream()
                .map(entry -> {
                    List<SaleItem> items = entry.getValue();
                    BigDecimal catTotal = items.stream().map(SaleItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    Long itemsSold = (long) items.size();
                    Long txCount = items.stream().map(item -> item.getSale().getId()).distinct().count();
                    return SalesReportResponse.CategorySalesSummary.builder()
                            .category(entry.getKey())
                            .totalSales(catTotal)
                            .itemsSold(itemsSold)
                            .transactionCount(txCount)
                            .build();
                })
                .sorted(Comparator.comparing(SalesReportResponse.CategorySalesSummary::getTotalSales).reversed())
                .collect(Collectors.toList());

        // By metal
        Map<String, List<SaleItem>> salesByMetal = sales.stream()
                .flatMap(sale -> sale.getItems().stream())
                .collect(Collectors.groupingBy(SaleItem::getMetalName));

        List<SalesReportResponse.MetalSalesSummary> byMetal = salesByMetal.entrySet().stream()
                .map(entry -> {
                    List<SaleItem> items = entry.getValue();
                    BigDecimal metalTotal = items.stream().map(SaleItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    Long itemsSold = (long) items.size();
                    Long txCount = items.stream().map(item -> item.getSale().getId()).distinct().count();
                    return SalesReportResponse.MetalSalesSummary.builder()
                            .metal(entry.getKey())
                            .totalSales(metalTotal)
                            .itemsSold(itemsSold)
                            .transactionCount(txCount)
                            .build();
                })
                .sorted(Comparator.comparing(SalesReportResponse.MetalSalesSummary::getTotalSales).reversed())
                .collect(Collectors.toList());

        // By salesperson
        Map<String, List<Sale>> salesByUser = sales.stream()
                .collect(Collectors.groupingBy(sale -> sale.getCreatedBy().getUsername()));

        List<SalesReportResponse.SalespersonSummary> bySalesperson = salesByUser.entrySet().stream()
                .map(entry -> {
                    List<Sale> userSales = entry.getValue();
                    BigDecimal userTotal = userSales.stream().map(Sale::getGrandTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
                    Long userTxCount = (long) userSales.size();
                    Long userItems = userSales.stream().flatMap(s -> s.getItems().stream()).count();
                    User user = userRepository.findByUsername(entry.getKey()).orElse(null);
                    String fullName = user != null ? user.getUsername() : entry.getKey();

                    return SalesReportResponse.SalespersonSummary.builder()
                            .username(entry.getKey())
                            .fullName(fullName)
                            .totalSales(userTotal)
                            .transactionCount(userTxCount)
                            .itemsSold(userItems)
                            .build();
                })
                .sorted(Comparator.comparing(SalesReportResponse.SalespersonSummary::getTotalSales).reversed())
                .collect(Collectors.toList());

        return SalesReportResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalSales(totalSales)
                .totalDiscount(totalDiscount)
                .totalGst(totalGst)
                .totalNetSales(totalNetSales)
                .totalTransactions(totalTransactions)
                .totalItemsSold(totalItemsSold)
                .dailySales(dailySales)
                .byCategory(byCategory)
                .byMetal(byMetal)
                .bySalesperson(bySalesperson)
                .build();
    }

    @Transactional(readOnly = true)
    public InventoryReportResponse getInventoryReport() {
        List<InventoryItem> items = inventoryItemRepository.findAll();
        LocalDate asOfDate = LocalDate.now();

        BigDecimal totalValue = items.stream()
                .map(InventoryItem::getPurchaseCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalItems = (long) items.size();
        Long availableItems = items.stream().filter(i -> i.getStatus() == InventoryItemStatus.AVAILABLE).count();
        Long reservedItems = items.stream().filter(i -> i.getStatus() == InventoryItemStatus.RESERVED).count();
        Long soldItems = items.stream().filter(i -> i.getStatus() == InventoryItemStatus.SOLD).count();
        Long damagedItems = items.stream().filter(i -> i.getStatus() == InventoryItemStatus.DAMAGED).count();

        // By category
        Map<String, List<InventoryItem>> byCategoryMap = items.stream()
                .collect(Collectors.groupingBy(item -> {
                    Jewellery j = item.getJewellery();
                    return j != null && j.getCategory() != null ? j.getCategory().getName() : "Unknown";
                }));

        List<InventoryReportResponse.CategoryInventorySummary> byCategory = byCategoryMap.entrySet().stream()
                .map(entry -> {
                    List<InventoryItem> catItems = entry.getValue();
                    Long catTotal = (long) catItems.size();
                    Long catAvailable = catItems.stream().filter(i -> i.getStatus() == InventoryItemStatus.AVAILABLE).count();
                    BigDecimal catValue = catItems.stream().map(InventoryItem::getPurchaseCost).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal avgValue = catTotal > 0 ? catValue.divide(BigDecimal.valueOf(catTotal), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

                    return InventoryReportResponse.CategoryInventorySummary.builder()
                            .category(entry.getKey())
                            .totalItems(catTotal)
                            .availableItems(catAvailable)
                            .totalValue(catValue)
                            .averageValue(avgValue)
                            .build();
                })
                .sorted(Comparator.comparing(InventoryReportResponse.CategoryInventorySummary::getTotalValue).reversed())
                .collect(Collectors.toList());

        // By metal
        Map<String, List<InventoryItem>> byMetalMap = items.stream()
                .collect(Collectors.groupingBy(item -> {
                    Jewellery j = item.getJewellery();
                    return j != null && j.getMetal() != null ? j.getMetal().getName() : "Unknown";
                }));

        List<InventoryReportResponse.MetalInventorySummary> byMetal = byMetalMap.entrySet().stream()
                .map(entry -> {
                    List<InventoryItem> metalItems = entry.getValue();
                    Long metalTotal = (long) metalItems.size();
                    Long metalAvailable = metalItems.stream().filter(i -> i.getStatus() == InventoryItemStatus.AVAILABLE).count();
                    BigDecimal metalWeight = metalItems.stream().map(InventoryItem::getNetWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal metalValue = metalItems.stream().map(InventoryItem::getPurchaseCost).reduce(BigDecimal.ZERO, BigDecimal::add);

                    return InventoryReportResponse.MetalInventorySummary.builder()
                            .metal(entry.getKey())
                            .totalItems((long) metalItems.size())
                            .availableItems(metalAvailable)
                            .totalWeight(metalWeight)
                            .totalValue(metalValue)
                            .build();
                })
                .sorted(Comparator.comparing(InventoryReportResponse.MetalInventorySummary::getTotalValue).reversed())
                .collect(Collectors.toList());

        // Aging (simplified - by creation date)
        LocalDate now = LocalDate.now();
        // 0-30 days
        Long count0_30 = items.stream().filter(i -> i.getCreatedAt() != null && i.getCreatedAt().toLocalDate().isAfter(now.minusDays(30))).count();
        BigDecimal val0_30 = items.stream().filter(i -> i.getCreatedAt() != null && i.getCreatedAt().toLocalDate().isAfter(now.minusDays(30)))
                .map(InventoryItem::getPurchaseCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 30-90 days
        Long count30_90 = items.stream().filter(i -> i.getCreatedAt() != null && i.getCreatedAt().toLocalDate().isAfter(now.minusDays(90)) && !i.getCreatedAt().toLocalDate().isAfter(now.minusDays(30))).count();
        BigDecimal val30_90 = items.stream().filter(i -> i.getCreatedAt() != null && i.getCreatedAt().toLocalDate().isAfter(now.minusDays(90)) && !i.getCreatedAt().toLocalDate().isAfter(now.minusDays(30)))
                .map(InventoryItem::getPurchaseCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 90+ days
        Long count90plus = items.stream().filter(i -> i.getCreatedAt() != null && i.getCreatedAt().toLocalDate().isBefore(now.minusDays(90))).count();
        BigDecimal val90plus = items.stream().filter(i -> i.getCreatedAt() != null && i.getCreatedAt().toLocalDate().isBefore(now.minusDays(90)))
                .map(InventoryItem::getPurchaseCost).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<InventoryReportResponse.AgingSummary> aging = List.of(
                InventoryReportResponse.AgingSummary.builder().range("0-30 days").itemCount(count0_30).totalValue(val0_30).build(),
                InventoryReportResponse.AgingSummary.builder().range("30-90 days").itemCount(count30_90).totalValue(val30_90).build(),
                InventoryReportResponse.AgingSummary.builder().range("90+ days").itemCount(count90plus).totalValue(val90plus).build()
        );

        BigDecimal totalInventoryValue = items.stream().map(InventoryItem::getPurchaseCost).reduce(BigDecimal.ZERO, BigDecimal::add);

        return InventoryReportResponse.builder()
                .asOfDate(LocalDate.now())
                .totalInventoryValue(totalInventoryValue)
                .totalItems((long) items.size())
                .availableItems(items.stream().filter(i -> i.getStatus() == InventoryItemStatus.AVAILABLE).count())
                .reservedItems(items.stream().filter(i -> i.getStatus() == InventoryItemStatus.RESERVED).count())
                .soldItems(items.stream().filter(i -> i.getStatus() == InventoryItemStatus.SOLD).count())
                .damagedItems(items.stream().filter(i -> i.getStatus() == InventoryItemStatus.DAMAGED).count())
                .byCategory(byCategory)
                .byMetal(byMetal)
                .aging(aging)
                .build();
    }

    @Transactional(readOnly = true)
    public FinancialReportResponse getFinancialReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Sale> sales = saleRepository.findBySaleDateBetween(start, end);

        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = sales.stream()
                .flatMap(sale -> sale.getItems().stream())
                .map(item -> {
                    InventoryItem inv = inventoryItemRepository.findById(item.getInventoryItemId()).orElse(null);
                    return inv != null ? inv.getPurchaseCost() : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = sales.stream().map(Sale::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGst = sales.stream().map(Sale::getGstAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal grossProfit = totalRevenue.subtract(totalCost);
        BigDecimal netProfit = grossProfit.subtract(sales.stream().map(Sale::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        // GST breakdown
        Map<BigDecimal, List<Sale>> gstMap = sales.stream()
                .collect(Collectors.groupingBy(Sale::getGstRate));

        List<FinancialReportResponse.GstSummary> gstBreakdown = gstMap.entrySet().stream()
                .map(entry -> {
                    BigDecimal rate = entry.getKey();
                    List<Sale> rateSales = entry.getValue();
                    BigDecimal taxable = rateSales.stream().map(Sale::getTaxableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal gstAmt = rateSales.stream().map(Sale::getGstAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal total = rateSales.stream().map(Sale::getGrandTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

                    return FinancialReportResponse.GstSummary.builder()
                            .gstRate(entry.getKey().setScale(2).toString() + "%")
                            .taxableAmount(taxable)
                            .gstAmount(gstAmt)
                            .totalAmount(total)
                            .build();
                })
                .collect(Collectors.toList());

        // Payment methods
        Map<String, List<Payment>> paymentMap = sales.stream()
                .flatMap(sale -> sale.getPayments().stream())
                .collect(Collectors.groupingBy(Payment::getPaymentMethod));

        List<FinancialReportResponse.PaymentMethodSummary> paymentMethods = paymentMap.entrySet().stream()
                .map(entry -> {
                    BigDecimal totalAmt = entry.getValue().stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    return FinancialReportResponse.PaymentMethodSummary.builder()
                            .method(entry.getKey())
                            .totalAmount(totalAmt)
                            .transactionCount((long) entry.getValue().size())
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal totalRevenueCalc = sales.stream().map(Sale::getGrandTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCostCalc = sales.stream()
                .flatMap(s -> s.getItems().stream())
                .map(item -> inventoryItemRepository.findById(item.getInventoryItemId())
                        .map(InventoryItem::getPurchaseCost).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscountCalc = sales.stream().map(Sale::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGstCalc = sales.stream().map(Sale::getGstAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal grossProfitCalc = totalRevenueCalc.subtract(totalCostCalc);
        BigDecimal netProfitCalc = grossProfitCalc.subtract(sales.stream().map(Sale::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        return FinancialReportResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenueCalc)
                .totalCost(totalCostCalc)
                .grossProfit(grossProfitCalc)
                .totalGstCollected(totalGstCalc)
                .totalDiscounts(totalDiscountCalc)
                .netProfit(netProfitCalc)
                .gstBreakdown(gstBreakdown)
                .paymentMethods(paymentMethods)
                .build();
}
}