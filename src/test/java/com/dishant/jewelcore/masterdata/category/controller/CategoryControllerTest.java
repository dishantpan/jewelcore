package com.dishant.jewelcore.masterdata.category.controller;

import com.dishant.jewelcore.common.exception.GlobalExceptionHandler;
import com.dishant.jewelcore.masterdata.category.dto.CategoryResponse;
import com.dishant.jewelcore.masterdata.category.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    private CategoryResponse response() {

        return new CategoryResponse(
                1L,
                "Rings",
                "RING",
                true,
                null,
                null
        );
    }

    @Test
    void shouldCreateCategory() throws Exception {

        when(categoryService.createCategory(any()))
                .thenReturn(response());

        mockMvc.perform(
                        post("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "Rings",
                                    "code": "RING"
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Category created successfully"))
                .andExpect(jsonPath("$.data.name")
                        .value("Rings"))
                .andExpect(jsonPath("$.data.code")
                        .value("RING"))
                .andExpect(jsonPath("$.data.active")
                        .value(true));
    }

    @Test
    void shouldGetCategoryById() throws Exception {

        when(categoryService.getCategoryById(1L))
                .thenReturn(response());

        mockMvc.perform(
                        get("/api/v1/categories/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name")
                        .value("Rings"))
                .andExpect(jsonPath("$.data.code")
                        .value("RING"));
    }

    @Test
    void shouldUpdateCategory() throws Exception {

        when(categoryService.updateCategory(
                any(Long.class),
                any()
        ))
                .thenReturn(response());

        mockMvc.perform(
                        put("/api/v1/categories/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "Gold Rings",
                                    "code": "RING"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Category updated successfully"))
                .andExpect(jsonPath("$.data.code")
                        .value("RING"));
    }

    @Test
    void shouldDeactivateCategory() throws Exception {

        doNothing()
                .when(categoryService)
                .deactivateCategory(1L);

        mockMvc.perform(
                        delete("/api/v1/categories/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Category deactivated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {

        mockMvc.perform(
                        post("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "",
                                    "code": ""
                                }
                                """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Invalid request data"));
    }
}