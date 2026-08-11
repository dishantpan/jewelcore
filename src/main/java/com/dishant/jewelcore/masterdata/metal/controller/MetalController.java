package com.dishant.jewelcore.masterdata.metal.controller;

import com.dishant.jewelcore.common.response.ApiResponse;
import com.dishant.jewelcore.masterdata.metal.dto.MetalCreateRequest;
import com.dishant.jewelcore.masterdata.metal.dto.MetalResponse;
import com.dishant.jewelcore.masterdata.metal.dto.MetalUpdateRequest;
import com.dishant.jewelcore.masterdata.metal.service.MetalService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metals")
public class MetalController {

    private final MetalService metalService;

    public MetalController(MetalService metalService) {
        this.metalService = metalService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MetalResponse>> createMetal(
            @Valid @RequestBody MetalCreateRequest request) {

        MetalResponse response = metalService.createMetal(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Metal created successfully", response));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<MetalResponse>>> getAllMetals() {

        List<MetalResponse> metals = metalService.getAllMetals();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Metals fetched successfully",
                        metals
                )
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MetalResponse>> getMetalById(
            @PathVariable Long id) {

        MetalResponse response = metalService.getMetalById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Metal fetched successfully",
                        response
                )
        );
    }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<MetalResponse>> updateMetal(
                @PathVariable Long id,
                @Valid @RequestBody MetalUpdateRequest request) {

            MetalResponse response = metalService.updateMetal(id, request);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Metal updated successfully",
                            response
                    )
            );
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateMetal(
            @PathVariable Long id) {

        metalService.deactivateMetal(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Metal deactivated successfully",
                        null
                )
        );
    }
    }
