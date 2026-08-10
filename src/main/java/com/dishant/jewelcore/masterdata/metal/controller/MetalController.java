package com.dishant.jewelcore.masterdata.metal.controller;

import com.dishant.jewelcore.common.response.ApiResponse;
import com.dishant.jewelcore.masterdata.metal.dto.MetalCreateRequest;
import com.dishant.jewelcore.masterdata.metal.dto.MetalResponse;
import com.dishant.jewelcore.masterdata.metal.service.MetalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/metals")
public class MetalController {

    private final MetalService metalService;

    public MetalController(MetalService metalService) {
        this.metalService = metalService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MetalResponse>> createMetal(
            @RequestBody MetalCreateRequest request) {

        MetalResponse response = metalService.createMetal(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Metal created successfully", response));
    }
}