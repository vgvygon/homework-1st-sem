package com.example.gateway.service;

import com.example.gateway.config.PasswordProperties;
import com.example.gateway.dto.LoginRequest;
import com.example.gateway.dto.LoginResponse;
import com.example.gateway.security.JwtUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordProperties passwordProperties;
    private final JwtUtils jwtUtils;

    public AuthService(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            PasswordProperties passwordProperties,
            JwtUtils jwtUtils
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.passwordProperties = passwordProperties;
        this.jwtUtils = jwtUtils;
    }

    public LoginResponse login(LoginRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        if (!passwordEncoder.matches(request.password() + passwordProperties.pepper(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new LoginResponse(jwtUtils.generateToken(userDetails.getUsername(), userDetails.getAuthorities()), "Bearer");
    }
}
