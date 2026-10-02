package com.osulaa.possystem.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.ExpiredJwtException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "MzQ3ODk0NTYxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=";

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                SECRET);

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                86400000L);

        jwtService.init();
    }

    @Test
    void generateToken_deberiaGenerarToken() {

        String token = jwtService.generateToken(
                "admin",
                "ADMIN");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_deberiaExtraerUsernameDelToken() {

        String token = jwtService.generateToken(
                "admin",
                "ADMIN");

        String username = jwtService.extractUsername(token);

        assertEquals("admin", username);
    }

    @Test
    void isTokenValid_deberiaRegresarTrueParaTokenValido() {

        String token = jwtService.generateToken(
                "admin",
                "ADMIN");

        boolean result = jwtService.isTokenValid(
                token,
                "admin");

        assertTrue(result);
    }

    @Test
    void isTokenValid_deberiaRegresarFalseParaUsernameIncorrecto() {

        String token = jwtService.generateToken(
                "admin",
                "ADMIN");

        boolean result = jwtService.isTokenValid(
                token,
                "otroUsuario");

        assertFalse(result);
    }

    @Test
    void isTokenValid_deberiaLanzarExcepcionParaTokenExpirado() {

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                -1000L);

        jwtService.init();

        String token = jwtService.generateToken(
                "admin",
                "ADMIN");

        assertThrows(
                ExpiredJwtException.class,
                () -> jwtService.isTokenValid(token, "admin"));
    }

    @Test
    void generateToken_deberiaIncluirRole() {

        String token = jwtService.generateToken(
                "admin",
                "ADMIN");

        byte[] keyBytes = Decoders.BASE64.decode(SECRET);

        var key = Keys.hmacShaKeyFor(keyBytes);

        String role = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);

        assertEquals("ADMIN", role);
    }
}