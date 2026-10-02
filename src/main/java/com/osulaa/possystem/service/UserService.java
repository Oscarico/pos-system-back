package com.osulaa.possystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PageMapper pageMapper;

    public PageResponse<UserResponse> findAll(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users;

        if (search == null || search.isBlank()) {
            users = userRepository.findByActiveTrue(pageable);
        } else {
            users = userRepository.findByNameContainingIgnoreCaseAndActiveTrue(search, pageable);
        }

        return pageMapper.toPageResponse(users, userMapper::toResponse);
    }

    public UserResponse findById(Long id) {
        User user = getUser(id);

        return userMapper.toResponse(user);
    }

    public UserResponse save(UserRequest request) {
        if (userRepository.findByUsernameAndActiveTrue(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("El usuario ya existe.");
        }

        User user = userMapper.toEntity(request);

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado."));

        user.setRole(role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);

        return userMapper.toResponse(saved);
    }

    public UserResponse update(Long id, UserUpdateRequest request) {
        User current = getUser(id);

        if (!current.getUsername().equalsIgnoreCase(request.getUsername())
                && userRepository.findByUsernameAndActiveTrue(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("El nombre de usuario ya existe.");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado."));
        current.setRole(role);

        userMapper.updateEntity(current, request);
        User updated = userRepository.save(current);

        return userMapper.toResponse(updated);
    }

    public void delete(Long id) {
        User user = getUser(id);

        user.setActive(false);

        userRepository.save(user);
    }

    public void changuePassword(
            String username,
            ChanguePasswordRequest request) {
        User user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("La contraseña actual es incorrecta.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    private User getUser(Long id) {
        return userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

}
