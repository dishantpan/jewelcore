package com.dishant.jewelcore.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private SecretKey createSecretKey() {

        return new SecretKeySpec(
                "this-is-a-development-secret-key-that-is-long-enough"
                        .getBytes(),
                "HmacSHA256"
        );
    }

    @Test
    void shouldGenerateJwtToken() {

        SecretKey secretKey = createSecretKey();

        JwtService jwtService = new JwtService(secretKey);

        String token = jwtService.generateToken(
                "owner",
                "OWNER"
        );

        assertThat(token).isNotBlank();
        assertThat(token.split("\\."))
                .hasSize(3);
    }

    @Test
    void shouldGenerateTokenWithCorrectClaims() {

        SecretKey secretKey = createSecretKey();

        JwtService jwtService = new JwtService(secretKey);

        String token = jwtService.generateToken(
                "owner",
                "OWNER"
        );

        JwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .build();

        Jwt jwt = decoder.decode(token);

        assertThat(jwt.getSubject())
                .isEqualTo("owner");

        assertThat(jwt.getClaimAsString("role"))
                .isEqualTo("OWNER");

        assertThat(jwt.getIssuedAt())
                .isNotNull();

        assertThat(jwt.getExpiresAt())
                .isNotNull();
    }
}