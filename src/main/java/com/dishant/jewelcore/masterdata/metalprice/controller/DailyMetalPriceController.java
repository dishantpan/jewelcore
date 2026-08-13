package com.dishant.jewelcore.masterdata.metalprice.controller;

import com.dishant.jewelcore.common.response.ApiResponse;
import com.dishant.jewelcore.masterdata.metalprice.dto.DailyMetalPriceCreateRequest;
import com.dishant.jewelcore.masterdata.metalprice.dto.DailyMetalPriceResponse;
import com.dishant.jewelcore.masterdata.metalprice.dto.DailyMetalPriceUpdateRequest;
import com.dishant.jewelcore.masterdata.metalprice.service.DailyMetalPriceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metal-prices")
public class DailyMetalPriceController {

    private final DailyMetalPriceService priceService;

    public DailyMetalPriceController(
            DailyMetalPriceService priceService) {

        this.priceService = priceService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DailyMetalPriceResponse>>
    createPrice(
            @Valid @RequestBody DailyMetalPriceCreateRequest request) {

        DailyMetalPriceResponse response =
                priceService.createPrice(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Daily metal price created successfully",
                                response
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DailyMetalPriceResponse>>
    getPriceById(@PathVariable Long id) {

        DailyMetalPriceResponse response =
                priceService.getPriceById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Daily metal price fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/metal/{metalId}")
    public ResponseEntity<ApiResponse<List<DailyMetalPriceResponse>>>
    getPricesByMetal(@PathVariable Long metalId) {

        List<DailyMetalPriceResponse> prices =
                priceService.getPricesByMetal(metalId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Daily metal prices fetched successfully",
                        prices
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DailyMetalPriceResponse>>
    updatePrice(
            @PathVariable Long id,
            @Valid @RequestBody DailyMetalPriceUpdateRequest request) {

        DailyMetalPriceResponse response =
                priceService.updatePrice(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Daily metal price updated successfully",
                        response
                )
        );
    }
}