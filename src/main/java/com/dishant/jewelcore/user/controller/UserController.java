package com.dishant.jewelcore.user.controller;

import com.dishant.jewelcore.common.response.ApiResponse;
import com.dishant.jewelcore.user.dto.UserCreateRequest;
import com.dishant.jewelcore.user.dto.UserResponse;
import com.dishant.jewelcore.user.entity.User;
import com.dishant.jewelcore.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserCreateRequest request) {

        User user = userService.createUser(
                request.username(),
                request.password(),
                request.role()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "User created successfully",
                                UserResponse.from(user)
                        )
                );
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<User> users = userService.getAllUsers();

        List<UserResponse> responses = users.stream()
                .map(UserResponse::from)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Users fetched successfully",
                        responses
                )
        );
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(
            @PathVariable String username) {

        User user = userService.getUserByUsername(username);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User fetched successfully",
                        UserResponse.from(user)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
            @PathVariable Long id) {

        userService.deactivateUser(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User deactivated successfully",
                        null
                )
        );
    }
}