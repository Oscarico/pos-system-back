package com.osulaa.possystem.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.dto.user.ChanguePasswordRequest;
import com.osulaa.possystem.dto.user.UserRequest;
import com.osulaa.possystem.dto.user.UserResponse;
import com.osulaa.possystem.dto.user.UserUpdateRequest;
import com.osulaa.possystem.entity.Role;
import com.osulaa.possystem.entity.User;
import com.osulaa.possystem.exception.DuplicateResourceException;
import com.osulaa.possystem.exception.InvalidCredentialsException;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.PageMapper;
import com.osulaa.possystem.mapper.UserMapper;
import com.osulaa.possystem.repository.RoleRepository;
import com.osulaa.possystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PageMapper pageMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role role;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {

        role = Role.builder()
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
                .active(true)
                .roleId(1L)
                .roleName("ADMIN")
                .build();
    }

    // =========================================================
    // findAll
    // =========================================================

    @SuppressWarnings("unchecked")
    @Test
    void findAll_deberiaRegresarUsuariosSinBusqueda() {

        Page<User> page = new PageImpl<>(List.of(user));

        PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                .items(List.of(userResponse))
                .page(0)
                .size(10)
                .totalItems(1L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(userRepository.findByActiveTrue(any(Pageable.class)))
                .thenReturn(page);

        when(pageMapper.toPageResponse(
                any(Page.class),
                any()))
                .thenReturn(pageResponse);

        PageResponse<UserResponse> response = userService.findAll(0, 10, "name", "asc", "");

        assertNotNull(response);

        verify(userRepository).findByActiveTrue(any(Pageable.class));
        verify(pageMapper).toPageResponse(any(Page.class), any());
    }

    @SuppressWarnings({ "null", "unchecked" })
    @Test
    void findAll_deberiaBuscarPorNombre() {

        Page<User> page = new PageImpl<>(List.of(user));

        PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                .items(List.of(userResponse))
                .page(0)
                .size(10)
                .totalItems(1L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(userRepository.findByNameContainingIgnoreCaseAndActiveTrue(
                anyString(),
                any(Pageable.class)))
                .thenReturn(page);

        when(pageMapper.toPageResponse(
                any(Page.class),
                any()))
                .thenReturn(pageResponse);

        PageResponse<UserResponse> response = userService.findAll(0, 10, "name", "asc", "admin");

        assertNotNull(response);

        // Corrección aquí: eq("admin")
        verify(userRepository)
                .findByNameContainingIgnoreCaseAndActiveTrue(
                        eq("admin"),
                        any(Pageable.class));
    }

    // =========================================================
    // findById
    // =========================================================

    @Test
    void findById_deberiaRegresarUsuario() {

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse response = userService.findById(1L);

        assertNotNull(response);
        assertEquals("admin", response.getUsername());

        verify(userRepository).findByIdAndActiveTrue(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    void findById_deberiaLanzarExcepcionSiNoExiste() {

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findById(1L));

        verify(userRepository).findByIdAndActiveTrue(1L);
    }

    // =========================================================
    // save
    // =========================================================

    @SuppressWarnings("null")
    @Test
    void save_deberiaCrearUsuarioCorrectamente() {

        UserRequest request = new UserRequest();
        request.setUsername("nuevo");
        request.setPassword("Password123");
        request.setName("Nuevo");
        request.setLastName("Usuario");
        request.setRoleId(1L);

        User newUser = User.builder()
                .username("nuevo")
                .password("Password123")
                .name("Nuevo")
                .lastName("Usuario")
                .active(true)
                .build();

        User savedUser = User.builder()
                .id(2L)
                .username("nuevo")
                .password("encoded-password")
                .name("Nuevo")
                .lastName("Usuario")
                .active(true)
                .role(role)
                .build();

        UserResponse response = UserResponse.builder()
                .id(2L)
                .username("nuevo")
                .name("Nuevo")
                .lastName("Usuario")
                .active(true)
                .roleId(1L)
                .roleName("ADMIN")
                .build();

        when(userRepository.findByUsernameAndActiveTrue("nuevo"))
                .thenReturn(Optional.empty());

        when(userMapper.toEntity(request))
                .thenReturn(newUser);

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(passwordEncoder.encode("Password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(newUser))
                .thenReturn(savedUser);

        when(userMapper.toResponse(savedUser))
                .thenReturn(response);

        UserResponse result = userService.save(request);

        assertNotNull(result);
        assertEquals("nuevo", result.getUsername());

        verify(userRepository).findByUsernameAndActiveTrue("nuevo");
        verify(roleRepository).findById(1L);
        verify(passwordEncoder).encode("Password123");
        verify(userRepository).save(newUser);
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    void save_deberiaLanzarExcepcionSiUsuarioYaExiste() {

        UserRequest request = new UserRequest();
        request.setUsername("admin");

        when(userRepository.findByUsernameAndActiveTrue("admin"))
                .thenReturn(Optional.of(user));

        assertThrows(
                DuplicateResourceException.class,
                () -> userService.save(request));

        verify(userRepository)
                .findByUsernameAndActiveTrue("admin");
    }

    @Test
    void save_deberiaLanzarExcepcionSiRolNoExiste() {

        UserRequest request = new UserRequest();
        request.setUsername("nuevo");
        request.setPassword("Password123");
        request.setRoleId(99L);

        User newUser = User.builder()
                .username("nuevo")
                .build();

        when(userRepository.findByUsernameAndActiveTrue("nuevo"))
                .thenReturn(Optional.empty());

        when(userMapper.toEntity(request))
                .thenReturn(newUser);

        when(roleRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.save(request));

        verify(roleRepository).findById(99L);
    }

    // =========================================================
    // update
    // =========================================================

    @Test
    void update_deberiaActualizarUsuarioCorrectamente() {

        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("adminUpdated");
        request.setName("Administrador Nuevo");
        request.setLastName("Sistema Nuevo");
        request.setRoleId(1L);

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse response = userService.update(1L, request);

        assertNotNull(response);

        verify(userRepository).findByIdAndActiveTrue(1L);
        verify(roleRepository).findById(1L);
        verify(userMapper).updateEntity(user, request);
        verify(userRepository).save(user);
    }

    @Test
    void update_deberiaLanzarExcepcionSiUsernameYaExiste() {

        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("otroUsuario");
        request.setName("Administrador");
        request.setLastName("Sistema");
        request.setRoleId(1L);

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.findByUsernameAndActiveTrue("otroUsuario"))
                .thenReturn(Optional.of(
                        User.builder()
                                .id(2L)
                                .username("otroUsuario")
                                .build()));

        assertThrows(
                DuplicateResourceException.class,
                () -> userService.update(1L, request));

        verify(userRepository)
                .findByUsernameAndActiveTrue("otroUsuario");
    }

    @Test
    void update_deberiaLanzarExcepcionSiRolNoExiste() {

        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("admin");
        request.setName("Administrador");
        request.setLastName("Sistema");
        request.setRoleId(99L);

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        when(roleRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.update(1L, request));

        verify(roleRepository).findById(99L);
    }

    // =========================================================
    // delete
    // =========================================================

    @Test
    void delete_deberiaDesactivarUsuario() {

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        userService.delete(1L);

        assertEquals(false, user.getActive());

        verify(userRepository).save(user);
    }

    @Test
    void delete_deberiaLanzarExcepcionSiNoExiste() {

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.delete(1L));

        verify(userRepository).findByIdAndActiveTrue(1L);
    }

    // =========================================================
    // changuePassword
    // =========================================================

    @Test
    void changuePassword_deberiaCambiarPasswordCorrectamente() {

        ChanguePasswordRequest request = new ChanguePasswordRequest();
        request.setCurrentPassword("Admin123");
        request.setNewPassword("NewPassword123");

        when(userRepository.findByUsernameAndActiveTrue("admin"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "Admin123",
                "encoded-password"))
                .thenReturn(true);

        when(passwordEncoder.encode("NewPassword123"))
                .thenReturn("new-encoded-password");

        userService.changuePassword("admin", request);

        assertEquals(
                "new-encoded-password",
                user.getPassword());

        verify(passwordEncoder)
                .matches("Admin123", "encoded-password");

        verify(passwordEncoder)
                .encode("NewPassword123");

        verify(userRepository).save(user);
    }

    @Test
    void changuePassword_deberiaLanzarExcepcionSiUsuarioNoExiste() {

        ChanguePasswordRequest request = new ChanguePasswordRequest();
        request.setCurrentPassword("Admin123");
        request.setNewPassword("NewPassword123");

        when(userRepository.findByUsernameAndActiveTrue("admin"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.changuePassword("admin", request));

        verify(userRepository)
                .findByUsernameAndActiveTrue("admin");
    }

    @Test
    void changuePassword_deberiaLanzarExcepcionSiPasswordActualEsIncorrecta() {

        ChanguePasswordRequest request = new ChanguePasswordRequest();
        request.setCurrentPassword("WrongPassword");
        request.setNewPassword("NewPassword123");

        when(userRepository.findByUsernameAndActiveTrue("admin"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword",
                "encoded-password"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.changuePassword("admin", request));

        verify(passwordEncoder)
                .matches("WrongPassword", "encoded-password");
    }
}