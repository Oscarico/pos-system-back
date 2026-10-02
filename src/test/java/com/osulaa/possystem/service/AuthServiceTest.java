package com.osulaa.possystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.osulaa.possystem.dto.auth.LoginRequest;
import com.osulaa.possystem.dto.auth.LoginResponse;
import com.osulaa.possystem.dto.user.UserResponse;
import com.osulaa.possystem.entity.Role;
import com.osulaa.possystem.entity.User;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.UserMapper;
import com.osulaa.possystem.repository.UserRepository;
import com.osulaa.possystem.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {

        loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("Admin123");

        Role role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        user = User.builder()
                .id(1L)
                .username("admin")
                .password("encoded-password")
                .name("Administrador")
                .lastName("Sistema")
                .active(true)
                .role(role)
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .username("admin")
                .name("Administrador")
                .lastName("Sistema")
                .roleId(1L)
                .roleName("ADMIN")
                .active(true)
                .build();
    }

    @Test
    void login_deberiaAutenticarYGenerarToken() {

        Authentication authenticated = new UsernamePasswordAuthenticationToken(
                "admin",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticated);

        when(jwtService.generateToken("admin", "ADMIN"))
                .thenReturn("jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class));

        verify(jwtService).generateToken("admin", "ADMIN");
    }

    @Test
    void getCurrentUser_deberiaRegresarUsuario() {

        when(userRepository.findByUsernameAndActiveTrue("admin"))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse response = authService.getCurrentUser("admin");

        assertNotNull(response);
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRoleName());

        verify(userRepository).findByUsernameAndActiveTrue("admin");
        verify(userMapper).toResponse(user);
    }

    @Test
    void getCurrentUser_deberiaLanzarExcepcionSiNoExiste() {

        when(userRepository.findByUsernameAndActiveTrue("admin"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> authService.getCurrentUser("admin"));

        verify(userRepository).findByUsernameAndActiveTrue("admin");
    }
}