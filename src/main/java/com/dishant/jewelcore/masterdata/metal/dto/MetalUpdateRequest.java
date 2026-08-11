package com.dishant.jewelcore.masterdata.metal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MetalUpdateRequest(

        @NotBlank(message = "Metal name is required")
        @Size(max = 50, message = "Metal name must not exceed 50 characters")
        String name,

        @NotBlank(message = "Metal code is required")
        @Size(max = 20, message = "Metal code must not exceed 20 characters")
        String code
) {
}