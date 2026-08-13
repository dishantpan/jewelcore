package com.dishant.jewelcore.pricing.controller;

import com.dishant.jewelcore.common.response.ApiResponse;
import com.dishant.jewelcore.pricing.dto.PriceCalculationRequest;
import com.dishant.jewelcore.pricing.dto.PriceCalculationResponse;
import com.dishant.jewelcore.pricing.service.PricingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<PriceCalculationResponse>> calculatePrice(
            @Valid @RequestBody PriceCalculationRequest request) {

        PriceCalculationResponse response =
                pricingService.calculatePrice(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Price calculated successfully",
                        response
                )
        );
    }
}