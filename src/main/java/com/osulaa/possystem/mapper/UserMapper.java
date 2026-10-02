package com.osulaa.possystem.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.osulaa.possystem.dto.user.UserRequest;
import com.osulaa.possystem.dto.user.UserResponse;
import com.osulaa.possystem.dto.user.UserUpdateRequest;
import com.osulaa.possystem.entity.User;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .name(request.getName())
                .lastName(request.getLastName())
                .active(true)
                .build();
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .lastName(user.getLastName())
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateEntity(User user, UserUpdateRequest request) {

        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setLastName(request.getLastName());
    }
}
