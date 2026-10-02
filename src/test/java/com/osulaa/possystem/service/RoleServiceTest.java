package com.osulaa.possystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.osulaa.possystem.dto.role.RoleRequest;
import com.osulaa.possystem.dto.role.RoleResponse;
import com.osulaa.possystem.entity.Role;
import com.osulaa.possystem.exception.DuplicateResourceException;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.RoleMapper;
import com.osulaa.possystem.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleResponse roleResponse;
    private RoleRequest roleRequest;

    @BeforeEach
    void setUp() {

        role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        roleResponse = RoleResponse.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        roleRequest = new RoleRequest();
        roleRequest.setName("ADMIN");
    }

    @Test
    void findAll_deberiaRegresarTodosLosRoles() {

        List<Role> roles = List.of(role);
        List<RoleResponse> responses = List.of(roleResponse);

        when(roleRepository.findAll())
                .thenReturn(roles);

        when(roleMapper.toResponseList(roles))
                .thenReturn(responses);

        List<RoleResponse> result = roleService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getName());

        verify(roleRepository).findAll();
        verify(roleMapper).toResponseList(roles);
    }

    @Test
    void findById_deberiaRegresarRol() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(roleMapper.toResponse(role))
                .thenReturn(roleResponse);

        RoleResponse result = roleService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ADMIN", result.getName());

        verify(roleRepository).findById(1L);
        verify(roleMapper).toResponse(role);
    }

    @Test
    void findById_deberiaLanzarExcepcionSiNoExiste() {

        when(roleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.findById(1L));

        verify(roleRepository).findById(1L);
    }

    @Test
    void save_deberiaGuardarRol() {

        when(roleRepository.findByName("ADMIN"))
                .thenReturn(Optional.empty());

        when(roleMapper.toEntity(roleRequest))
                .thenReturn(role);

        when(roleRepository.save(role))
                .thenReturn(role);

        when(roleMapper.toResponse(role))
                .thenReturn(roleResponse);

        RoleResponse result = roleService.save(roleRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ADMIN", result.getName());

        verify(roleRepository).findByName("ADMIN");
        verify(roleMapper).toEntity(roleRequest);
        verify(roleRepository).save(role);
        verify(roleMapper).toResponse(role);
    }

    @Test
    void save_deberiaLanzarExcepcionSiRolYaExiste() {

        when(roleRepository.findByName("ADMIN"))
                .thenReturn(Optional.of(role));

        assertThrows(
                DuplicateResourceException.class,
                () -> roleService.save(roleRequest));

        verify(roleRepository).findByName("ADMIN");
    }
}