package com.dishant.jewelcore.controller;

import com.dishant.jewelcore.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success(
                "JewelCore is running",
                "OK"
        );
    }
    @GetMapping("/error-test")
    public ApiResponse<String> errorTest() {
        throw new RuntimeException("Test exception");
    }
}