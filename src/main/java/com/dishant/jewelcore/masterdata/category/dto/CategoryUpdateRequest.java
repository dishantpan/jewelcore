package com.dishant.jewelcore.masterdata.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Category code is required")
        @Size(max = 30)
        String code
) {
}