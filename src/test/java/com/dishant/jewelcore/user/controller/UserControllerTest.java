package com.dishant.jewelcore.user.controller;

import com.dishant.jewelcore.user.entity.User;
import com.dishant.jewelcore.user.entity.UserRole;
import com.dishant.jewelcore.user.service.UserService;
import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {

        User user = new User(
                "owner",
                "password",
                UserRole.OWNER
        );

        when(userService.createUser(
                "owner",
                "password",
                UserRole.OWNER
        )).thenReturn(user);

        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "username": "owner",
                                    "password": "password",
                                    "role": "OWNER"
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("User created successfully"))
                .andExpect(jsonPath("$.data.username")
                        .value("owner"))
                .andExpect(jsonPath("$.data.role")
                        .value("OWNER"))
                .andExpect(jsonPath("$.data.active")
                        .value(true))
                .andExpect(jsonPath("$.data.password")
                        .doesNotExist());
    }

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {

        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "username": "",
                                    "password": "123",
                                    "role": null
                                }
                                """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetUserByUsername() throws Exception {

        User user = new User(
                "owner",
                "password",
                UserRole.OWNER
        );

        when(userService.getUserByUsername("owner"))
                .thenReturn(user);

        mockMvc.perform(
                        get("/api/v1/users/owner")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("User fetched successfully"))
                .andExpect(jsonPath("$.data.username")
                        .value("owner"))
                .andExpect(jsonPath("$.data.role")
                        .value("OWNER"))
                .andExpect(jsonPath("$.data.active")
                        .value(true))
                .andExpect(jsonPath("$.data.password")
                        .doesNotExist());
    }
    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {

        when(userService.getUserByUsername("missing"))
                .thenThrow(new ResourceNotFoundException(
                        "User not found: missing"
                ));

        mockMvc.perform(
                        get("/api/v1/users/missing")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("User not found: missing"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldDeactivateUser() throws Exception {

        mockMvc.perform(
                        delete("/api/v1/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("User deactivated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}