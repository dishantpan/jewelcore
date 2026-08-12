package com.dishant.jewelcore.user.service;

import com.dishant.jewelcore.user.entity.User;
import com.dishant.jewelcore.user.entity.UserRole;
import com.dishant.jewelcore.user.repository.UserRepository;
import com.dishant.jewelcore.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String password, UserRole role) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Username already exists: " + username
            );
        }

        User user = new User(
                username,
                password,
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