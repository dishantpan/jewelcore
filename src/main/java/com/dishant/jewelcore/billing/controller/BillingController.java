package com.dishant.jewelcore.billing.controller;

import com.dishant.jewelcore.billing.dto.SaleCreateRequest;
import com.dishant.jewelcore.billing.dto.SaleResponse;
import com.dishant.jewelcore.billing.entity.Sale;
import com.dishant.jewelcore.billing.service.SaleService;
import com.dishant.jewelcore.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillingController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponse>> createBill(@Valid @RequestBody SaleCreateRequest request) {
        String username = getCurrentUsername();
        SaleResponse response = saleService.createSale(request, username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bill created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getAllBills() {
        List<SaleResponse> bills = saleService.getAllSales().stream()
                .map(saleService::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Bills fetched", bills));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaleResponse>> getBillById(@PathVariable Long id) {
        Sale sale = saleService.getSaleById(id);
        SaleResponse bill = saleService.mapToResponse(sale);
        return ResponseEntity.ok(ApiResponse.success("Bill fetched", bill));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getBillsByCustomer(@PathVariable Long customerId) {
        List<SaleResponse> bills = saleService.getSalesByCustomer(customerId).stream()
                .map(saleService::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Customer bills fetched", bills));
    }

    private String getCurrentUsername() {
        // In production, get from SecurityContext
        return "system";
    }
}