package com.dishant.jewelcore.security;

import com.dishant.jewelcore.user.entity.User;
import com.dishant.jewelcore.user.entity.UserRole;
import com.dishant.jewelcore.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void shouldLoadActiveOwnerUser() {

        User user = new User(
                "owner",
                "encoded-password",
                UserRole.OWNER
        );

        when(userRepository.findByUsername("owner"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                userDetailsService.loadUserByUsername("owner");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("owner");
        assertThat(result.getPassword())
                .isEqualTo("encoded-password");
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_OWNER");

        verify(userRepository).findByUsername("owner");
    }

    @Test
    void shouldLoadActiveSalespersonUser() {

        User user = new User(
                "salesperson",
                "encoded-password",
                UserRole.SALESPERSON
        );

        when(userRepository.findByUsername("salesperson"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                userDetailsService.loadUserByUsername("salesperson");

        assertThat(result).isNotNull();
        assertThat(result.getUsername())
                .isEqualTo("salesperson");
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_SALESPERSON");

        verify(userRepository).findByUsername("salesperson");
    }

    @Test
    void shouldDisableInactiveUser() {

        User user = new User(
                "owner",
                "encoded-password",
                UserRole.OWNER
        );

        user.deactivate();

        when(userRepository.findByUsername("owner"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                userDetailsService.loadUserByUsername("owner");

        assertThat(result).isNotNull();
        assertThat(result.isEnabled()).isFalse();
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_OWNER");

        verify(userRepository).findByUsername("owner");
    }

    @Test
    void shouldRejectUnknownUser() {

        when(userRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userDetailsService.loadUserByUsername("missing")
        )
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: missing");

        verify(userRepository).findByUsername("missing");
    }
}