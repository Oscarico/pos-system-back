package com.osulaa.possystem.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void securityFilterChain_deberiaEstarConfigurado() {

        assertNotNull(securityFilterChain);
    }

    @Test
    void authenticationManager_deberiaEstarConfigurado() {

        assertNotNull(authenticationManager);
    }

    @Test
    void passwordEncoder_deberiaEstarConfigurado() {

        assertNotNull(passwordEncoder);
    }

    @Test
    void passwordEncoder_deberiaEncriptarPassword() {

        String password = "password123";

        String encodedPassword = passwordEncoder.encode(password);

        assertNotNull(encodedPassword);
        assertTrue(
                passwordEncoder.matches(
                        password,
                        encodedPassword));
    }
}