package com.osulaa.possystem.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.osulaa.possystem.entity.Role;
import com.osulaa.possystem.entity.User;
import com.osulaa.possystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {

        Role role = Role.builder()
                .name("ADMIN")
                .build();

        user = User.builder()
                .username("admin")
                .password("password123")
                .name("Administrador")
                .lastName("Test")
                .active(true)
                .role(role)
                .build();
    }

    @Test
    void loadUserByUsername_deberiaEncontrarUsuario() {

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        assertEquals("admin", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertTrue(result.isEnabled());
    }

    @Test
    void loadUserByUsername_deberiaAsignarRolCorrectamente() {

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_deberiaRespetarEstadoActivo() {

        user.setActive(false);

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        assertTrue(!result.isEnabled());
    }

    @Test
    void loadUserByUsername_deberiaLanzarExcepcionSiNoExiste() {

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("admin"));
    }
}