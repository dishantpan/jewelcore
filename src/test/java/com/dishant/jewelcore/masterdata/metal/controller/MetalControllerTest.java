package com.dishant.jewelcore.masterdata.metal.controller;

import com.dishant.jewelcore.common.exception.GlobalExceptionHandler;
import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.common.response.ApiResponse;
import com.dishant.jewelcore.masterdata.metal.dto.MetalCreateRequest;
import com.dishant.jewelcore.masterdata.metal.dto.MetalResponse;
import com.dishant.jewelcore.masterdata.metal.dto.MetalUpdateRequest;
import com.dishant.jewelcore.masterdata.metal.service.MetalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MetalController.class)
@Import(GlobalExceptionHandler.class)
class MetalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MetalService metalService;

    @Test
    void shouldCreateMetal() throws Exception {

        MetalResponse response = new MetalResponse(
                1L,
                "Silver",
                "SIL",
                true
        );

        when(metalService.createMetal(any(MetalCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/metals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Silver",
                                    "code": "SIL"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Metal created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Silver"))
                .andExpect(jsonPath("$.data.code").value("SIL"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void shouldGetAllMetals() throws Exception {

        MetalResponse response = new MetalResponse(
                1L,
                "Silver",
                "SIL",
                true
        );

        when(metalService.getAllMetals())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/metals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Metals fetched successfully"))
                .andExpect(jsonPath("$.data[0].name").value("Silver"))
                .andExpect(jsonPath("$.data[0].code").value("SIL"));
    }

    @Test
    void shouldGetMetalById() throws Exception {

        MetalResponse response = new MetalResponse(
                1L,
                "Silver",
                "SIL",
                true
        );

        when(metalService.getMetalById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/metals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Metal fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Silver"));
    }

    @Test
    void shouldReturnNotFoundWhenMetalDoesNotExist() throws Exception {

        when(metalService.getMetalById(99L))
                .thenThrow(new ResourceNotFoundException(
                        "Metal not found with id: 99"
                ));

        mockMvc.perform(get("/api/v1/metals/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Metal not found with id: 99"));
    }

    @Test
    void shouldUpdateMetal() throws Exception {

        MetalResponse response = new MetalResponse(
                1L,
                "Pure Silver",
                "SIL",
                true
        );

        when(metalService.updateMetal(
                any(Long.class),
                any(MetalUpdateRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/api/v1/metals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Pure Silver",
                                    "code": "SIL"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Metal updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Pure Silver"))
                .andExpect(jsonPath("$.data.code").value("SIL"));
    }

    @Test
    void shouldDeactivateMetal() throws Exception {

        doNothing().when(metalService).deactivateMetal(1L);

        mockMvc.perform(delete("/api/v1/metals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Metal deactivated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {

        mockMvc.perform(post("/api/v1/metals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "",
                                    "code": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}