package com.dishant.jewelcore.auth.service;

import com.dishant.jewelcore.auth.dto.LoginRequest;
import com.dishant.jewelcore.auth.dto.LoginResponse;
import com.dishant.jewelcore.security.JwtService;
import com.dishant.jewelcore.user.entity.User;
import com.dishant.jewelcore.user.entity.UserRole;
import com.dishant.jewelcore.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request = new LoginRequest(
                "owner",
                "password"
        );

        User user = new User(
                "owner",
                "encoded-password",
                UserRole.OWNER
        );

        when(userService.getUserByUsername("owner"))
                .thenReturn(user);

        when(jwtService.generateToken("owner", "OWNER"))
                .thenReturn("test-jwt-token");

        LoginResponse result = authService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("owner");
        assertThat(result.role()).isEqualTo(UserRole.OWNER);
        assertThat(result.token()).isEqualTo("test-jwt-token");

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verify(userService).getUserByUsername("owner");

        verify(jwtService).generateToken(
                "owner",
                "OWNER"
        );
    }

    @Test
    void shouldNotLoadUserOrGenerateTokenWhenAuthenticationFails() {

        LoginRequest request = new LoginRequest(
                "owner",
                "wrong-password"
        );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        ))
                .thenThrow(new RuntimeException("Authentication failed"));

        assertThatThrownBy(() ->
                authService.login(request)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Authentication failed");

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verify(userService, never())
                .getUserByUsername("owner");

        verify(jwtService, never())
                .generateToken(any(), any());
    }
}