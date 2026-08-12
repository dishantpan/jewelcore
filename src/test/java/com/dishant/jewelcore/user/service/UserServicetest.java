package com.dishant.jewelcore.user.service;

import com.dishant.jewelcore.user.entity.User;
import com.dishant.jewelcore.user.entity.UserRole;
import com.dishant.jewelcore.user.repository.UserRepository;
import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserWhenUsernameDoesNotExist() {

        when(userRepository.findByUsername("owner"))
                .thenReturn(Optional.empty());

        User savedUser = new User(
                "owner",
                "password",
                UserRole.OWNER
        );

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = userService.createUser(
                "owner",
                "password",
                UserRole.OWNER
        );

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("owner");
        assertThat(result.getRole()).isEqualTo(UserRole.OWNER);
        assertThat(result.isActive()).isTrue();

        verify(userRepository).findByUsername("owner");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRejectUserWhenUsernameAlreadyExists() {

        User existingUser = new User(
                "owner",
                "password",
                UserRole.OWNER
        );

        when(userRepository.findByUsername("owner"))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() ->
                userService.createUser(
                        "owner",
                        "anotherPassword",
                        UserRole.SALESPERSON
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists: owner");

        verify(userRepository).findByUsername("owner");
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void shouldDeactivateActiveUser() {

        User user = new User(
                "owner",
                "password",
                UserRole.OWNER
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deactivateUser(1L);

        assertThat(user.isActive()).isFalse();

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void shouldRejectDeactivationWhenUserDoesNotExist() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.deactivateUser(1L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 1");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectDeactivationWhenUserIsAlreadyInactive() {

        User user = new User(
                "owner",
                "password",
                UserRole.OWNER
        );

        user.deactivate();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                userService.deactivateUser(1L)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User is already inactive: 1");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
}