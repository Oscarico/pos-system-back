package com.osulaa.possystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osulaa.possystem.dto.role.RoleRequest;
import com.osulaa.possystem.dto.role.RoleResponse;
import com.osulaa.possystem.security.CustomUserDetailsService;
import com.osulaa.possystem.security.JwtService;
import com.osulaa.possystem.service.RoleService;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // Helper para construir DTOs de respuesta ficticios
    private RoleResponse createRoleResponse(Long id, String name) {
        return RoleResponse.builder()
                .id(id)
                .name(name)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAll_deberiaRegresarListaDeRoles() throws Exception {
        RoleResponse role1 = createRoleResponse(1L, "ROLE_ADMIN");
        RoleResponse role2 = createRoleResponse(2L, "ROLE_CASHIER");

        when(roleService.findAll()).thenReturn(List.of(role1, role2));

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lista de roles obtenida correctamente."))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("ROLE_CASHIER"));

        verify(roleService).findAll();
    }

    @Test
    void getById_deberiaRegresarRolPorId() throws Exception {
        RoleResponse role = createRoleResponse(1L, "ROLE_ADMIN");

        when(roleService.findById(1L)).thenReturn(role);

        mockMvc.perform(get("/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rol encontrado."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("ROLE_ADMIN"));

        verify(roleService).findById(1L);
    }

    @Test
    void save_deberiaCrearRolExitosamente() throws Exception {
        RoleRequest request = new RoleRequest();
        request.setName("ROLE_ADMIN");

        RoleResponse response = createRoleResponse(1L, "ROLE_ADMIN");

        when(roleService.save(any(RoleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Rol creado correctamente."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("ROLE_ADMIN"));

        verify(roleService).save(any(RoleRequest.class));
    }

    @Test
    void save_deberiaRechazarNombreVacio() throws Exception {
        RoleRequest invalidRequest = new RoleRequest();
        invalidRequest.setName(""); // Rompe la validación @NotBlank

        mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).save(any(RoleRequest.class));
    }
}