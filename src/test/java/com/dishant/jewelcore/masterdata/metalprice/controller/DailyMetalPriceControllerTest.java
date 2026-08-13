package com.dishant.jewelcore.masterdata.metalprice.controller;

import com.dishant.jewelcore.common.exception.GlobalExceptionHandler;
import com.dishant.jewelcore.masterdata.metalprice.dto.DailyMetalPriceResponse;
import com.dishant.jewelcore.masterdata.metalprice.service.DailyMetalPriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DailyMetalPriceController.class)
@Import(GlobalExceptionHandler.class)
class DailyMetalPriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DailyMetalPriceService priceService;

    private DailyMetalPriceResponse response() {

        return new DailyMetalPriceResponse(
                1L,
                1L,
                "Gold",
                "GOLD",
                LocalDate.of(2026, 8, 13),
                new BigDecimal("7250.00"),
                null,
                null
        );
    }

    @Test
    void shouldCreateDailyMetalPrice() throws Exception {

        when(priceService.createPrice(any()))
                .thenReturn(response());

        mockMvc.perform(
                        post("/api/v1/metal-prices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "metalId": 1,
                                    "priceDate": "2026-08-13",
                                    "pricePerGram": 7250.00
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Daily metal price created successfully"
                        ))
                .andExpect(jsonPath("$.data.metalId")
                        .value(1))
                .andExpect(jsonPath("$.data.metalName")
                        .value("Gold"))
                .andExpect(jsonPath("$.data.metalCode")
                        .value("GOLD"))
                .andExpect(jsonPath("$.data.pricePerGram")
                        .value(7250.00));
    }

    @Test
    void shouldGetPriceById() throws Exception {

        when(priceService.getPriceById(1L))
                .thenReturn(response());

        mockMvc.perform(
                        get("/api/v1/metal-prices/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data.id")
                        .value(1))
                .andExpect(jsonPath("$.data.metalCode")
                        .value("GOLD"))
                .andExpect(jsonPath("$.data.pricePerGram")
                        .value(7250.00));
    }

    @Test
    void shouldGetPricesByMetal() throws Exception {

        when(priceService.getPricesByMetal(1L))
                .thenReturn(List.of(response()));

        mockMvc.perform(
                        get("/api/v1/metal-prices/metal/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data")
                        .isArray())
                .andExpect(jsonPath("$.data[0].metalCode")
                        .value("GOLD"))
                .andExpect(jsonPath("$.data[0].metalName")
                        .value("Gold"))
                .andExpect(jsonPath("$.data[0].pricePerGram")
                        .value(7250.00));
    }

    @Test
    void shouldUpdatePrice() throws Exception {

        when(priceService.updatePrice(
                any(Long.class),
                any()
        ))
                .thenReturn(response());

        mockMvc.perform(
                        put("/api/v1/metal-prices/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "pricePerGram": 7300.00
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Daily metal price updated successfully"
                        ))
                .andExpect(jsonPath("$.data.pricePerGram")
                        .value(7250.00));
    }

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {

        mockMvc.perform(
                        post("/api/v1/metal-prices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "metalId": null,
                                    "priceDate": null,
                                    "pricePerGram": null
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