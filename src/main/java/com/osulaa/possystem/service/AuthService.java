package com.osulaa.possystem.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.osulaa.possystem.dto.auth.LoginRequest;
import com.osulaa.possystem.dto.auth.LoginResponse;
import com.osulaa.possystem.dto.user.UserResponse;
import com.osulaa.possystem.entity.User;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.UserMapper;
import com.osulaa.possystem.repository.UserRepository;
import com.osulaa.possystem.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String username = authentication.getName();

        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", "");

        String token = jwtService.generateToken(username, role);

        return new LoginResponse(token);
    }

    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        return userMapper.toResponse(user);
    }
}
