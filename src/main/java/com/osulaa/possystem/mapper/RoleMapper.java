package com.osulaa.possystem.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.osulaa.possystem.dto.role.RoleRequest;
import com.osulaa.possystem.dto.role.RoleResponse;
import com.osulaa.possystem.entity.Role;

@Component
public class RoleMapper {

    public Role toEntity(RoleRequest request) {
        return Role.builder()
                .name(request.getName())
                .build();
    }

    public RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    public List<RoleResponse> toResponseList(List<Role> roles) {
        return roles.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateEntity(Role role, RoleRequest request) {

        role.setName(request.getName());
    }
}
