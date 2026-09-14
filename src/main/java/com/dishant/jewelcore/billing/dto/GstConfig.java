package com.dishant.jewelcore.billing.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GstConfig {

    private BigDecimal gstRate;
    private String hsnCode;
    private String description;

    public static GstConfig defaultConfig() {
        return GstConfig.builder()
                .gstRate(new BigDecimal("3.00"))
                .hsnCode("7113")
                .description("Jewellery under HSN 7113")
                .build();
    }
}