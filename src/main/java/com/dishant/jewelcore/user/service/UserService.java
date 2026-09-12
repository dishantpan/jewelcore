package com.dishant.jewelcore.user.service;

import java.util.List;
import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import com.dishant.jewelcore.user.entity.User;
import com.dishant.jewelcore.user.entity.UserRole;
import com.dishant.jewelcore.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(
            String username,
            String password,
            UserRole role) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Username already exists: " + username
            );
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(
                username,
                encodedPassword,
                role
        );

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username
                ));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void deactivateUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id
                ));

        if (!user.isActive()) {
            throw new IllegalStateException(
                    "User is already inactive: " + id
            );
        }

        user.deactivate();

        userRepository.save(user);
    }
}