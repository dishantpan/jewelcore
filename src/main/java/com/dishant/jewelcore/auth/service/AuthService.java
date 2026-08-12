package com.dishant.jewelcore.auth.service;

import com.dishant.jewelcore.auth.dto.LoginRequest;
import com.dishant.jewelcore.auth.dto.LoginResponse;
import com.dishant.jewelcore.security.JwtService;
import com.dishant.jewelcore.user.entity.User;
import com.dishant.jewelcore.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = userService.getUserByUsername(
                request.username()
        );

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return new LoginResponse(
                user.getUsername(),
                user.getRole(),
                token
        );
    }
}