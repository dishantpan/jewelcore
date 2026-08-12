package com.dishant.jewelcore.auth.controller;

import com.dishant.jewelcore.auth.dto.LoginResponse;
import com.dishant.jewelcore.auth.service.AuthService;
import com.dishant.jewelcore.common.exception.GlobalExceptionHandler;
import com.dishant.jewelcore.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully() throws Exception {

        when(authService.login(any()))
                .thenReturn(
                        new LoginResponse(
                                "owner",
                                UserRole.OWNER,
                                "test-jwt-token"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "username": "owner",
                                    "password": "password"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Login successful"))
                .andExpect(jsonPath("$.data.username")
                        .value("owner"))
                .andExpect(jsonPath("$.data.role")
                        .value("OWNER"))
                .andExpect(jsonPath("$.data.token")
                        .value("test-jwt-token"));
    }

    @Test
    void shouldRejectInvalidLoginRequest() throws Exception {

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "username": "",
                                    "password": ""
                                }
                                """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Invalid request data"));
    }
}