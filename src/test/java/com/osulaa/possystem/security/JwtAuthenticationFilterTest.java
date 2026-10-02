package com.osulaa.possystem.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {

        filter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService);

        userDetails = new org.springframework.security.core.userdetails.User(
                "admin",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {

        SecurityContextHolder.clearContext();
    }

    @Test
    void sinAuthorization_deberiaContinuarCadena() throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        filter.doFilter(
                request,
                response,
                filterChain);

        verify(filterChain).doFilter(
                request,
                response);

        verify(jwtService, never()).extractUsername("token");
        verify(userDetailsService, never()).loadUserByUsername("admin");

        assertNull(
                SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void authorizationSinBearer_deberiaContinuarCadena() throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn("Basic abc123");

        filter.doFilter(
                request,
                response,
                filterChain);

        verify(filterChain).doFilter(
                request,
                response);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenValido_deberiaEstablecerAutenticacion() throws ServletException, IOException {

        String token = "token-valido";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.extractUsername(token))
                .thenReturn("admin");

        when(userDetailsService.loadUserByUsername("admin"))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(
                token,
                userDetails.getUsername()))
                .thenReturn(true);

        filter.doFilter(
                request,
                response,
                filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertTrue(
                authentication instanceof UsernamePasswordAuthenticationToken);

        assertEquals(
                "admin",
                authentication.getName());

        assertEquals(
                userDetails,
                authentication.getPrincipal());

        assertEquals(
                "ROLE_ADMIN",
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority());

        verify(filterChain).doFilter(
                request,
                response);
    }

    @Test
    void tokenInvalido_deberiaLimpiarContexto() throws ServletException, IOException {

        String token = "token-invalido";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.extractUsername(token))
                .thenThrow(new RuntimeException("Token inválido"));

        filter.doFilter(
                request,
                response,
                filterChain);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(
                request,
                response);
    }

    @Test
    void tokenValidoPeroUsuarioYaAutenticado_noDeberiaVolverAautenticar()
            throws ServletException, IOException {

        String token = "token-valido";

        UsernamePasswordAuthenticationToken existingAuthentication = new UsernamePasswordAuthenticationToken(
                "usuario-existente",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        SecurityContextHolder.getContext()
                .setAuthentication(existingAuthentication);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.extractUsername(token))
                .thenReturn("admin");

        filter.doFilter(
                request,
                response,
                filterChain);

        assertSame(
                existingAuthentication,
                SecurityContextHolder.getContext().getAuthentication());

        // Sí se ejecuta porque está antes del if
        verify(jwtService).extractUsername(token);

        // Pero estas dos cosas NO deben ejecutarse
        verify(userDetailsService, never())
                .loadUserByUsername("admin");

        verify(jwtService, never())
                .isTokenValid(token, "admin");

        verify(filterChain).doFilter(
                request,
                response);
    }
}