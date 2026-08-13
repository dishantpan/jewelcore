package com.dishant.jewelcore.inventory.jewellery.controller;

import com.dishant.jewelcore.common.exception.GlobalExceptionHandler;
import com.dishant.jewelcore.inventory.jewellery.dto.JewelleryResponse;
import com.dishant.jewelcore.inventory.jewellery.service.JewelleryService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JewelleryController.class)
@Import(GlobalExceptionHandler.class)
class JewelleryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JewelleryService jewelleryService;

    private JewelleryResponse response() {

        return new JewelleryResponse(
                1L,
                "RG-001",
                "Classic Gold Ring",
                "Classic 22K gold ring",

                1L,
                "Rings",
                "RING",

                1L,
                "Gold",
                "GOLD",

                1L,
                "22 Karat Gold",
                "22K",

                new BigDecimal("5.200"),
                new BigDecimal("0.200"),
                new BigDecimal("5.000"),
                new BigDecimal("2500.00"),

                true,

                null,
                null
        );
    }

    @Test
    void shouldCreateJewellery() throws Exception {

        when(jewelleryService.createJewellery(any()))
                .thenReturn(response());

        mockMvc.perform(
                        post("/api/v1/jewellery")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "sku": "RG-001",
                                    "name": "Classic Gold Ring",
                                    "description": "Classic 22K gold ring",
                                    "categoryId": 1,
                                    "metalId": 1,
                                    "purityId": 1,
                                    "grossWeight": 5.200,
                                    "stoneWeight": 0.200,
                                    "netWeight": 5.000,
                                    "makingCharge": 2500.00
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Jewellery created successfully"))
                .andExpect(jsonPath("$.data.sku")
                        .value("RG-001"))
                .andExpect(jsonPath("$.data.categoryCode")
                        .value("RING"))
                .andExpect(jsonPath("$.data.metalCode")
                        .value("GOLD"))
                .andExpect(jsonPath("$.data.purityCode")
                        .value("22K"))
                .andExpect(jsonPath("$.data.netWeight")
                        .value(5.000))
                .andExpect(jsonPath("$.data.active")
                        .value(true));
    }

    @Test
    void shouldGetAllJewellery() throws Exception {

        when(jewelleryService.getAllJewellery())
                .thenReturn(List.of(response()));

        mockMvc.perform(
                        get("/api/v1/jewellery")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data")
                        .isArray())
                .andExpect(jsonPath("$.data[0].sku")
                        .value("RG-001"))
                .andExpect(jsonPath("$.data[0].metalCode")
                        .value("GOLD"));
    }

    @Test
    void shouldGetJewelleryById() throws Exception {

        when(jewelleryService.getJewelleryById(1L))
                .thenReturn(response());

        mockMvc.perform(
                        get("/api/v1/jewellery/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data.id")
                        .value(1))
                .andExpect(jsonPath("$.data.sku")
                        .value("RG-001"))
                .andExpect(jsonPath("$.data.purityCode")
                        .value("22K"));
    }

    @Test
    void shouldUpdateJewellery() throws Exception {

        when(jewelleryService.updateJewellery(
                any(Long.class),
                any()
        ))
                .thenReturn(response());

        mockMvc.perform(
                        put("/api/v1/jewellery/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "Updated Gold Ring",
                                    "description": "Updated ring",
                                    "categoryId": 1,
                                    "metalId": 1,
                                    "purityId": 1,
                                    "grossWeight": 5.200,
                                    "stoneWeight": 0.200,
                                    "netWeight": 5.000,
                                    "makingCharge": 3000.00
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.message")
                        .value("Jewellery updated successfully"))
                .andExpect(jsonPath("$.data.sku")
                        .value("RG-001"));
    }

    @Test
    void shouldDeactivateJewellery() throws Exception {

        mockMvc.perform(
                        delete("/api/v1/jewellery/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.message")
                        .value("Jewellery deactivated successfully"))
                .andExpect(jsonPath("$.data")
                        .doesNotExist());
    }

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {

        mockMvc.perform(
                        post("/api/v1/jewellery")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "sku": "",
                                    "name": "",
                                    "categoryId": null,
                                    "metalId": null,
                                    "purityId": null,
                                    "grossWeight": null,
                                    "stoneWeight": null,
                                    "netWeight": null,
                                    "makingCharge": null
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