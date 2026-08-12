package com.dishant.jewelcore.auth.dto;

import com.dishant.jewelcore.user.entity.UserRole;

public record LoginResponse(
        String username,
        UserRole role,
        String token
) {
}