package com.dishant.jewelcore.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationConverterTest {

    private final JwtAuthenticationConverter converter =
            new JwtAuthenticationConverter();

    @Test
    void shouldConvertOwnerRoleToAuthority() {

        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of(
                        "sub", "owner",
                        "role", "OWNER"
                )
        );

        Authentication authentication =
                converter.convert(jwt);

        assertThat(authentication).isNotNull();
        assertThat(authentication.getName())
                .isEqualTo("owner");

        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_OWNER");
    }

    @Test
    void shouldConvertSalespersonRoleToAuthority() {

        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of(
                        "sub", "salesperson",
                        "role", "SALESPERSON"
                )
        );

        Authentication authentication =
                converter.convert(jwt);

        assertThat(authentication.getName())
                .isEqualTo("salesperson");

        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_SALESPERSON");
    }

    @Test
    void shouldHaveNoAuthoritiesWhenRoleIsMissing() {

        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("sub", "owner")
        );

        Authentication authentication =
                converter.convert(jwt);

        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities())
                .isEmpty();
    }
}