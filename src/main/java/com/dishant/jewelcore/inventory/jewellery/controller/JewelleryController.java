package com.dishant.jewelcore.inventory.jewellery.controller;

import com.dishant.jewelcore.common.response.ApiResponse;
import com.dishant.jewelcore.inventory.jewellery.dto.JewelleryCreateRequest;
import com.dishant.jewelcore.inventory.jewellery.dto.JewelleryResponse;
import com.dishant.jewelcore.inventory.jewellery.dto.JewelleryUpdateRequest;
import com.dishant.jewelcore.inventory.jewellery.service.JewelleryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jewellery")
public class JewelleryController {

    private final JewelleryService jewelleryService;

    public JewelleryController(
            JewelleryService jewelleryService) {

        this.jewelleryService = jewelleryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JewelleryResponse>>
    createJewellery(
            @Valid @RequestBody JewelleryCreateRequest request) {

        JewelleryResponse response =
                jewelleryService.createJewellery(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Jewellery created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JewelleryResponse>>>
    getAllJewellery() {

        List<JewelleryResponse> jewellery =
                jewelleryService.getAllJewellery();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Jewellery fetched successfully",
                        jewellery
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JewelleryResponse>>
    getJewelleryById(@PathVariable Long id) {

        JewelleryResponse response =
                jewelleryService.getJewelleryById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Jewellery fetched successfully",
                        response
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JewelleryResponse>>
    updateJewellery(
            @PathVariable Long id,
            @Valid @RequestBody JewelleryUpdateRequest request) {

        JewelleryResponse response =
                jewelleryService.updateJewellery(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Jewellery updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>>
    deactivateJewellery(@PathVariable Long id) {

        jewelleryService.deactivateJewellery(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Jewellery deactivated successfully",
                        null
                )
        );
    }
}