package com.osulaa.possystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.osulaa.possystem.dto.auth.LoginRequest;
import com.osulaa.possystem.dto.auth.LoginResponse;
import com.osulaa.possystem.dto.user.UserResponse;
import com.osulaa.possystem.payload.ApiResponse;
import com.osulaa.possystem.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST: http://localhost:8080/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    // GET: http://localhost:8080/auth/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            Authentication authentication) {
        UserResponse user = authService.getCurrentUser(authentication.getName());

        return ResponseEntity.ok(
                ApiResponse.success("User data retrieved successfully", user));
    }
}
