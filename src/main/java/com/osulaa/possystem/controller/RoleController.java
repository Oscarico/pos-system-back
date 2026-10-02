package com.osulaa.possystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.osulaa.possystem.dto.role.RoleRequest;
import com.osulaa.possystem.dto.role.RoleResponse;
import com.osulaa.possystem.payload.ApiResponse;
import com.osulaa.possystem.service.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // GET: http://localhost:8080/roles
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAll() {
        List<RoleResponse> roles = roleService.findAll();

        return ResponseEntity.ok(
                ApiResponse.success("Lista de roles obtenida correctamente.", roles));
    }

    // GET: http://localhost:8080/roles/{id}
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> getById(@PathVariable Long id) {
        RoleResponse role = roleService.findById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Rol encontrado.", role));
    }

    // POST: http://localhost:8080/roles
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> save(@Valid @RequestBody RoleRequest request) {
        RoleResponse saved = roleService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success("Rol creado correctamente.", saved));
    }

}
