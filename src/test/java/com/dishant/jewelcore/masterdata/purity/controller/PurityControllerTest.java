package com.dishant.jewelcore.masterdata.purity.controller;

import com.dishant.jewelcore.common.exception.GlobalExceptionHandler;
import com.dishant.jewelcore.masterdata.purity.dto.PurityResponse;
import com.dishant.jewelcore.masterdata.purity.service.PurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurityController.class)
@Import(GlobalExceptionHandler.class)
class PurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurityService purityService;

    private PurityResponse response() {

        return new PurityResponse(
                1L,
                "24 Karat Gold",
                "24K",
                new BigDecimal("99.90"),
                true,
                null,
                null
        );
    }

    @Test
    void shouldCreatePurity() throws Exception {

        when(purityService.createPurity(any()))
                .thenReturn(response());

        mockMvc.perform(
                        post("/api/v1/purities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "24 Karat Gold",
                                    "code": "24K",
                                    "percentage": 99.90
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Purity created successfully"))
                .andExpect(jsonPath("$.data.name")
                        .value("24 Karat Gold"))
                .andExpect(jsonPath("$.data.code")
                        .value("24K"))
                .andExpect(jsonPath("$.data.percentage")
                        .value(99.90))
                .andExpect(jsonPath("$.data.active")
                        .value(true));
    }

    @Test
    void shouldGetPurityById() throws Exception {

        when(purityService.getPurityById(1L))
                .thenReturn(response());

        mockMvc.perform(
                        get("/api/v1/purities/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.code")
                        .value("24K"));
    }

    @Test
    void shouldUpdatePurity() throws Exception {

        when(purityService.updatePurity(
                any(Long.class),
                any()
        ))
                .thenReturn(response());

        mockMvc.perform(
                        put("/api/v1/purities/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "24 Karat Gold",
                                    "code": "24K",
                                    "percentage": 99.90
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Purity updated successfully"))
                .andExpect(jsonPath("$.data.code")
                        .value("24K"));
    }

    @Test
    void shouldDeactivatePurity() throws Exception {

        doNothing()
                .when(purityService)
                .deactivatePurity(1L);

        mockMvc.perform(
                        delete("/api/v1/purities/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Purity deactivated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {

        mockMvc.perform(
                        post("/api/v1/purities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "",
                                    "code": "",
                                    "percentage": null
                                }
                                """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Invalid request data"));
    }
}