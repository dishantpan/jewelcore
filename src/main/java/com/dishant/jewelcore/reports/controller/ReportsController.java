package com.dishant.jewelcore.reports.controller;

import com.dishant.jewelcore.reports.dto.FinancialReportResponse;
import com.dishant.jewelcore.reports.dto.InventoryReportResponse;
import com.dishant.jewelcore.reports.dto.SalesReportResponse;
import com.dishant.jewelcore.reports.service.ReportService;
import com.dishant.jewelcore.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportService reportService;

    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<SalesReportResponse>> getSalesReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        SalesReportResponse report = reportService.getSalesReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Sales report generated", report));
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<InventoryReportResponse>> getInventoryReport() {
        InventoryReportResponse report = reportService.getInventoryReport();
        return ResponseEntity.ok(ApiResponse.success("Inventory report generated", report));
    }

    @GetMapping("/financial")
    public ResponseEntity<ApiResponse<FinancialReportResponse>> getFinancialReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        FinancialReportResponse report = reportService.getFinancialReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Financial report generated", report));
    }
}