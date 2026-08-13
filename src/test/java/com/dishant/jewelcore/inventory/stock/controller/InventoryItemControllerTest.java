package com.dishant.jewelcore.inventory.stock.controller;

import com.dishant.jewelcore.common.exception.GlobalExceptionHandler;
import com.dishant.jewelcore.inventory.stock.dto.InventoryItemResponse;
import com.dishant.jewelcore.inventory.stock.entity.InventoryItemStatus;
import com.dishant.jewelcore.inventory.stock.service.InventoryItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryItemController.class)
@Import(GlobalExceptionHandler.class)
class InventoryItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryItemService inventoryItemService;

    private InventoryItemResponse response() {

        return new InventoryItemResponse(
                1L,
                "ITEM-001",

                1L,
                "RG-001",
                "Classic Gold Ring",

                new BigDecimal("5.200"),
                new BigDecimal("0.200"),
                new BigDecimal("5.000"),

                new BigDecimal("40000.00"),

                InventoryItemStatus.AVAILABLE,

                "SHOWCASE-A1",

                null,
                null
        );
    }

    @Test
    void shouldCreateInventoryItem() throws Exception {

        when(inventoryItemService.createItem(any()))
                .thenReturn(response());

        mockMvc.perform(
                        post("/api/v1/inventory-items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "itemCode": "ITEM-001",
                                    "jewelleryId": 1,
                                    "grossWeight": 5.200,
                                    "stoneWeight": 0.200,
                                    "netWeight": 5.000,
                                    "purchaseCost": 40000.00,
                                    "location": "SHOWCASE-A1"
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Inventory item created successfully"
                        ))
                .andExpect(jsonPath("$.data.itemCode")
                        .value("ITEM-001"))
                .andExpect(jsonPath("$.data.jewellerySku")
                        .value("RG-001"))
                .andExpect(jsonPath("$.data.status")
                        .value("AVAILABLE"));
    }

    @Test
    void shouldGetAllInventoryItems() throws Exception {

        when(inventoryItemService.getAllItems())
                .thenReturn(List.of(response()));

        mockMvc.perform(
                        get("/api/v1/inventory-items")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data")
                        .isArray())
                .andExpect(jsonPath("$.data[0].itemCode")
                        .value("ITEM-001"));
    }

    @Test
    void shouldGetInventoryItemById() throws Exception {

        when(inventoryItemService.getItemById(1L))
                .thenReturn(response());

        mockMvc.perform(
                        get("/api/v1/inventory-items/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data.id")
                        .value(1))
                .andExpect(jsonPath("$.data.itemCode")
                        .value("ITEM-001"))
                .andExpect(jsonPath("$.data.status")
                        .value("AVAILABLE"));
    }

    @Test
    void shouldGetItemsByStatus() throws Exception {

        when(inventoryItemService.getItemsByStatus(
                InventoryItemStatus.AVAILABLE
        ))
                .thenReturn(List.of(response()));

        mockMvc.perform(
                        get("/api/v1/inventory-items/status/AVAILABLE")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data")
                        .isArray())
                .andExpect(jsonPath("$.data[0].status")
                        .value("AVAILABLE"));
    }

    @Test
    void shouldGetItemsByJewellery() throws Exception {

        when(inventoryItemService.getItemsByJewellery(1L))
                .thenReturn(List.of(response()));

        mockMvc.perform(
                        get("/api/v1/inventory-items/jewellery/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data")
                        .isArray())
                .andExpect(jsonPath("$.data[0].jewellerySku")
                        .value("RG-001"));
    }

    @Test
    void shouldReserveInventoryItem() throws Exception {

        InventoryItemResponse reserved =
                new InventoryItemResponse(
                        1L,
                        "ITEM-001",
                        1L,
                        "RG-001",
                        "Classic Gold Ring",
                        new BigDecimal("5.200"),
                        new BigDecimal("0.200"),
                        new BigDecimal("5.000"),
                        new BigDecimal("40000.00"),
                        InventoryItemStatus.RESERVED,
                        "SHOWCASE-A1",
                        null,
                        null
                );

        when(inventoryItemService.reserveItem(1L))
                .thenReturn(reserved);

        mockMvc.perform(
                        put("/api/v1/inventory-items/1/reserve")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Inventory item reserved successfully"
                        ))
                .andExpect(jsonPath("$.data.status")
                        .value("RESERVED"));
    }

    @Test
    void shouldSellInventoryItem() throws Exception {

        InventoryItemResponse sold =
                new InventoryItemResponse(
                        1L,
                        "ITEM-001",
                        1L,
                        "RG-001",
                        "Classic Gold Ring",
                        new BigDecimal("5.200"),
                        new BigDecimal("0.200"),
                        new BigDecimal("5.000"),
                        new BigDecimal("40000.00"),
                        InventoryItemStatus.SOLD,
                        "SHOWCASE-A1",
                        null,
                        null
                );

        when(inventoryItemService.sellItem(1L))
                .thenReturn(sold);

        mockMvc.perform(
                        put("/api/v1/inventory-items/1/sell")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Inventory item sold successfully"
                        ))
                .andExpect(jsonPath("$.data.status")
                        .value("SOLD"));
    }

    @Test
    void shouldMarkInventoryItemDamaged() throws Exception {

        InventoryItemResponse damaged =
                new InventoryItemResponse(
                        1L,
                        "ITEM-001",
                        1L,
                        "RG-001",
                        "Classic Gold Ring",
                        new BigDecimal("5.200"),
                        new BigDecimal("0.200"),
                        new BigDecimal("5.000"),
                        new BigDecimal("40000.00"),
                        InventoryItemStatus.DAMAGED,
                        "SHOWCASE-A1",
                        null,
                        null
                );

        when(inventoryItemService.markItemDamaged(1L))
                .thenReturn(damaged);

        mockMvc.perform(
                        put("/api/v1/inventory-items/1/damage")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Inventory item marked as damaged"
                        ))
                .andExpect(jsonPath("$.data.status")
                        .value("DAMAGED"));
    }

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {

        mockMvc.perform(
                        post("/api/v1/inventory-items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "itemCode": "",
                                    "jewelleryId": null,
                                    "grossWeight": null,
                                    "stoneWeight": null,
                                    "netWeight": null,
                                    "purchaseCost": null
                                }
                                """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success")
                        .value(false))
                .andExpect(jsonPath("$.message")
                        .value("Invalid request data"));
    }
}