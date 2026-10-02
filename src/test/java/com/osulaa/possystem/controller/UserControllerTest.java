package com.osulaa.possystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.dto.user.ChanguePasswordRequest;
import com.osulaa.possystem.dto.user.UserRequest;
import com.osulaa.possystem.dto.user.UserResponse;
import com.osulaa.possystem.dto.user.UserUpdateRequest;
import com.osulaa.possystem.security.CustomUserDetailsService;
import com.osulaa.possystem.security.JwtService;
import com.osulaa.possystem.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityAutoConfiguration.class) // <--- Registra los resolvers de argumentos de Spring Security
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private UserResponse createUserResponse(Long id, String username, String name) {
        return UserResponse.builder()
                .id(id)
                .username(username)
                .name(name)
                .lastName("Pérez")
                .active(true)
                .roleId(1L)
                .roleName("ROLE_ADMIN")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void changuePassword_deberiaCambiarPasswordExitosamente() throws Exception {
        ChanguePasswordRequest request = new ChanguePasswordRequest();
        request.setCurrentPassword("password123");
        request.setNewPassword("newPassword123");

        doNothing().when(userService)
                .changuePassword(eq("admin"), any(ChanguePasswordRequest.class));

        mockMvc.perform(patch("/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(result -> {
                    System.out.println("========== DEBUG ==========");
                    System.out.println("STATUS: " + result.getResponse().getStatus());
                    System.out.println("BODY: " + result.getResponse().getContentAsString());
                    System.out.println("ERROR: " + result.getResolvedException());
                    System.out.println("===========================");
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contraseña actualizada correctamente"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(userService).changuePassword(eq("admin"), any(ChanguePasswordRequest.class));
    }

    @Test
    @WithMockUser(username = "adminUser", roles = { "ADMIN" })
    void changuePassword_deberiaRechazarPasswordCorta() throws Exception {

        ChanguePasswordRequest invalidRequest = new ChanguePasswordRequest();
        invalidRequest.setCurrentPassword("oldPassword123");
        invalidRequest.setNewPassword("short"); // Menor a 8 caracteres

        mockMvc.perform(patch("/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).changuePassword(any(), any());
    }

    @Test
    void getAll_deberiaRegresarPaginaDeUsuarios() throws Exception {
        UserResponse user1 = createUserResponse(1L, "johndoe", "John");
        UserResponse user2 = createUserResponse(2L, "janedoe", "Jane");

        PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                .items(List.of(user1, user2))
                .page(0)
                .size(10)
                .totalItems(2L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(userService.findAll(0, 10, "name", "asc", null)).thenReturn(pageResponse);

        mockMvc.perform(get("/users")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lista de usuarios obtenida exitosamente."))
                .andExpect(jsonPath("$.data.items.length()").value(2))
                .andExpect(jsonPath("$.data.items[0].username").value("johndoe"))
                .andExpect(jsonPath("$.data.items[1].username").value("janedoe"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(false));

        verify(userService).findAll(0, 10, "name", "asc", null);
    }

    @Test
    void getById_deberiaRegresarUsuarioPorId() throws Exception {
        UserResponse response = createUserResponse(1L, "johndoe", "John");

        when(userService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario encontrado."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("johndoe"));

        verify(userService).findById(1L);
    }

    @Test
    void save_deberiaCrearUsuarioExitosamente() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("johndoe");
        request.setPassword("securePassword123");
        request.setName("John");
        request.setLastName("Doe");
        request.setRoleId(1L);

        UserResponse response = createUserResponse(1L, "johndoe", "John");

        when(userService.save(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuario creado correctamente."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("johndoe"));

        verify(userService).save(any(UserRequest.class));
    }

    @Test
    void save_deberiaValidarCamposObligatorios() throws Exception {
        UserRequest invalidRequest = new UserRequest();

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(any());
    }

    @Test
    void update_deberiaActualizarUsuarioExitosamente() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("john_updated");
        request.setName("John");
        request.setLastName("Doe");
        request.setRoleId(1L);

        UserResponse response = createUserResponse(1L, "john_updated", "John");

        when(userService.update(eq(1L), any(UserUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario actualizado correctamente."))
                .andExpect(jsonPath("$.data.username").value("john_updated"));

        verify(userService).update(eq(1L), any(UserUpdateRequest.class));
    }

    @Test
    void delete_deberiaEliminarUsuarioExitosamente() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario eliminado correctamente."));

        verify(userService).delete(1L);
    }
}