package com.dishant.jewelcore.inventory.stock.controller;

import com.dishant.jewelcore.common.response.ApiResponse;
import com.dishant.jewelcore.inventory.stock.dto.InventoryItemCreateRequest;
import com.dishant.jewelcore.inventory.stock.dto.InventoryItemResponse;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItemStatus;
import com.dishant.jewelcore.inventory.stock.service.InventoryItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory-items")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    public InventoryItemController(
            InventoryItemService inventoryItemService) {

        this.inventoryItemService = inventoryItemService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryItemResponse>>
    createItem(
            @Valid @RequestBody InventoryItemCreateRequest request) {

        InventoryItemResponse response =
                inventoryItemService.createItem(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Inventory item created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<InventoryItemResponse>>>
    getAllItems() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Inventory items fetched successfully",
                        inventoryItemService.getAllItems()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryItemResponse>>
    getItemById(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Inventory item fetched successfully",
                        inventoryItemService.getItemById(id)
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<
            ApiResponse<List<InventoryItemResponse>>>
    getItemsByStatus(
            @PathVariable InventoryItemStatus status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Inventory items fetched successfully",
                        inventoryItemService
                                .getItemsByStatus(status)
                )
        );
    }

    @GetMapping("/jewellery/{jewelleryId}")
    public ResponseEntity<
            ApiResponse<List<InventoryItemResponse>>>
    getItemsByJewellery(
            @PathVariable Long jewelleryId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Inventory items fetched successfully",
                        inventoryItemService
                                .getItemsByJewellery(jewelleryId)
                )
        );
    }

    @PutMapping("/{id}/reserve")
    public ResponseEntity<ApiResponse<InventoryItemResponse>>
    reserveItem(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Inventory item reserved successfully",
                        inventoryItemService.reserveItem(id)
                )
        );
    }

    @PutMapping("/{id}/sell")
    public ResponseEntity<ApiResponse<InventoryItemResponse>>
    sellItem(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Inventory item sold successfully",
                        inventoryItemService.sellItem(id)
                )
        );
    }

    @PutMapping("/{id}/damage")
    public ResponseEntity<ApiResponse<InventoryItemResponse>>
    markItemDamaged(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Inventory item marked as damaged",
                        inventoryItemService.markItemDamaged(id)
                )
        );
    }
}