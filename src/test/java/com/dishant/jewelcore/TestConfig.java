package com.dishant.jewelcore;

import com.dishant.jewelcore.billing.dto.GstConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    public GstConfig gstConfig() {
        return GstConfig.builder()
                .gstRate(new java.math.BigDecimal("3.00"))
                .hsnCode("7113")
                .description("Jewellery under HSN 7113")
                .build();
    }
}