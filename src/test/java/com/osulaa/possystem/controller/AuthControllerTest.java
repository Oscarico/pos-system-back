package com.osulaa.possystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osulaa.possystem.dto.auth.LoginRequest;
import com.osulaa.possystem.dto.auth.LoginResponse;
import com.osulaa.possystem.dto.user.UserResponse;
import com.osulaa.possystem.security.CustomUserDetailsService;
import com.osulaa.possystem.security.JwtService;
import com.osulaa.possystem.service.AuthService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("password");

        loginResponse = new LoginResponse("jwt-token");

        userResponse = UserResponse.builder()
                .username("admin")
                .name("Administrador")
                .lastName("Test")
                .active(true)
                .roleId(1L)
                .roleName("ADMIN")
                .build();
    }

    @Test
    void login_deberiaAutenticarUsuario() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void getCurrentUser_deberiaRegresarUsuarioAutenticado() throws Exception {
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("admin");

        when(authService.getCurrentUser("admin"))
                .thenReturn(userResponse);

        mockMvc.perform(get("/auth/me")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.username").value("admin"));

        // Se ajusta a times(2) debido a la llamada interna de Spring FrameworkServlet +
        // el Controller
        verify(authentication, times(2)).getName();
        verify(authService).getCurrentUser(eq("admin"));
    }
}