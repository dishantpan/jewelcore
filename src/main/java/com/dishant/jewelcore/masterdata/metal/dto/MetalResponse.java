package com.dishant.jewelcore.masterdata.metal.dto;

import com.dishant.jewelcore.masterdata.metal.entity.Metal;

import java.time.LocalDateTime;

public record MetalResponse(
        Long id,
        String name,
        String code,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public MetalResponse(long id, String silver, String sil, boolean active) {
        this(id, silver, sil, active, null, null);
    }

    public static MetalResponse from(Metal metal) {
        return new MetalResponse(
                metal.getId(),
                metal.getName(),
                metal.getCode(),
                metal.isActive(),
                metal.getCreatedAt(),
                metal.getUpdatedAt()
        );
    }
}