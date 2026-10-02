package com.osulaa.possystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.osulaa.possystem.dto.role.RoleRequest;
import com.osulaa.possystem.dto.role.RoleResponse;
import com.osulaa.possystem.entity.Role;
import com.osulaa.possystem.exception.DuplicateResourceException;
import com.osulaa.possystem.exception.ResourceNotFoundException;
import com.osulaa.possystem.mapper.RoleMapper;
import com.osulaa.possystem.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleResponse> findAll() {
        return roleMapper.toResponseList(roleRepository.findAll());
    }

    public RoleResponse findById(Long id) {
        Role role = getRole(id);

        return roleMapper.toResponse(role);
    }

    public RoleResponse save(RoleRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("El rol ya existe.");
        }

        Role role = roleMapper.toEntity(request);
        Role saved = roleRepository.save(role);

        return roleMapper.toResponse(saved);
    }

    private Role getRole(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado."));
    }
}
