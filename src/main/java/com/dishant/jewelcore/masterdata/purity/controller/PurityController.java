package com.dishant.jewelcore.masterdata.purity.controller;

import com.dishant.jewelcore.common.response.ApiResponse;
import com.dishant.jewelcore.masterdata.purity.dto.PurityCreateRequest;
import com.dishant.jewelcore.masterdata.purity.dto.PurityResponse;
import com.dishant.jewelcore.masterdata.purity.dto.PurityUpdateRequest;
import com.dishant.jewelcore.masterdata.purity.service.PurityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purities")
public class PurityController {

    private final PurityService purityService;

    public PurityController(PurityService purityService) {
        this.purityService = purityService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PurityResponse>> createPurity(
            @Valid @RequestBody PurityCreateRequest request) {

        PurityResponse response =
                purityService.createPurity(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Purity created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PurityResponse>>> getAllPurities() {

        List<PurityResponse> purities =
                purityService.getAllPurities();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Purities fetched successfully",
                        purities
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurityResponse>> getPurityById(
            @PathVariable Long id) {

        PurityResponse response =
                purityService.getPurityById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Purity fetched successfully",
                        response
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PurityResponse>> updatePurity(
            @PathVariable Long id,
            @Valid @RequestBody PurityUpdateRequest request) {

        PurityResponse response =
                purityService.updatePurity(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Purity updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivatePurity(
            @PathVariable Long id) {

        purityService.deactivatePurity(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Purity deactivated successfully",
                        null
                )
        );
    }
}