package com.dishant.jewelcore.masterdata.category.dto;

import com.dishant.jewelcore.masterdata.category.entity.Category;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String code,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CategoryResponse from(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getCode(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}