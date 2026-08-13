package com.dishant.jewelcore.pricing.controller;

import com.dishant.jewelcore.common.exception.GlobalExceptionHandler;
import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.pricing.dto.PriceCalculationResponse;
import com.dishant.jewelcore.pricing.service.PricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PricingController.class)
@Import(GlobalExceptionHandler.class)
class PricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PricingService pricingService;

    private PriceCalculationResponse response() {

        return new PriceCalculationResponse(
                1L,
                "ITEM-001",

                1L,
                "Classic Gold Ring",

                "Gold",
                "GOLD",

                "22 Karat",
                "22K",
                new BigDecimal("91.60"),

                LocalDate.of(2026, 8, 13),
                new BigDecimal("7250.00"),

                new BigDecimal("5.000"),
                new BigDecimal("6641.00"),
                new BigDecimal("33205.00"),

                new BigDecimal("2500.00"),
                new BigDecimal("35705.00")
        );
    }

    @Test
    void shouldCalculatePriceSuccessfully() throws Exception {

        when(pricingService.calculatePrice(any()))
                .thenReturn(response());

        mockMvc.perform(
                        post("/api/v1/pricing/calculate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "inventoryItemId": 1,
                                    "priceDate": "2026-08-13"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Price calculated successfully"))
                .andExpect(jsonPath("$.data.inventoryItemId")
                        .value(1))
                .andExpect(jsonPath("$.data.itemCode")
                        .value("ITEM-001"))
                .andExpect(jsonPath("$.data.metalCode")
                        .value("GOLD"))
                .andExpect(jsonPath("$.data.purityCode")
                        .value("22K"))
                .andExpect(jsonPath("$.data.purityPercentage")
                        .value(91.60))
                .andExpect(jsonPath("$.data.metalRatePerGram")
                        .value(7250.00))
                .andExpect(jsonPath("$.data.netWeight")
                        .value(5.000))
                .andExpect(jsonPath("$.data.metalValue")
                        .value(33205.00))
                .andExpect(jsonPath("$.data.makingCharge")
                        .value(2500.00))
                .andExpect(jsonPath("$.data.finalPrice")
                        .value(35705.00));
    }

    @Test
    void shouldRejectInvalidRequest() throws Exception {

        mockMvc.perform(
                        post("/api/v1/pricing/calculate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "inventoryItemId": null,
                                    "priceDate": null
                                }
                                """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Invalid request data"));
    }

    @Test
    void shouldReturnNotFoundWhenPricingDataDoesNotExist()
            throws Exception {

        when(pricingService.calculatePrice(any()))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Inventory item not found with id: 99"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/pricing/calculate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "inventoryItemId": 99,
                                    "priceDate": "2026-08-13"
                                }
                                """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success")
                        .value(false));
    }
}