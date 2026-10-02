package com.osulaa.possystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.dto.user.ChanguePasswordRequest;
import com.osulaa.possystem.dto.user.UserRequest;
import com.osulaa.possystem.dto.user.UserResponse;
import com.osulaa.possystem.dto.user.UserUpdateRequest;
import com.osulaa.possystem.payload.ApiResponse;
import com.osulaa.possystem.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

        private final UserService userService;

        // PUT: http://localhost:8080/users/me/password
        @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
        @PatchMapping("/me/password")
        public ResponseEntity<ApiResponse<Void>> changuePassword(
                        @Valid @RequestBody ChanguePasswordRequest request) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                userService.changuePassword(username, request);

                return ResponseEntity.ok(
                                ApiResponse.success("Contraseña actualizada correctamente", null));
        }

        // GET:
        // http://localhost:8080/users?page=0&size=20&sortBy=name&sortDir=desc&search=admin
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping
        public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "name") String sortBy,
                        @RequestParam(defaultValue = "asc") String sortDir,
                        @RequestParam(required = false) String search) {
                PageResponse<UserResponse> users = userService.findAll(page, size, sortBy, sortDir, search);

                return ResponseEntity.ok(
                                ApiResponse.success("Lista de usuarios obtenida exitosamente.", users));
        }

        // GET: http://localhost:8080/users/{id}
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
                UserResponse user = userService.findById(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Usuario encontrado.", user));
        }

        // POST: http://localhost:8080/users
        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ResponseEntity<ApiResponse<UserResponse>> save(@Valid @RequestBody UserRequest request) {
                UserResponse saved = userService.save(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success("Usuario creado correctamente.", saved));
        }

        // PUT: http://localhost:8080/products/{id}
        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long id,
                        @Valid @RequestBody UserUpdateRequest request) {
                UserResponse updated = userService.update(id, request);

                return ResponseEntity.ok(
                                ApiResponse.success("Usuario actualizado correctamente.", updated));
        }

        // DELETE: http://localhost:8080/products/{id}
        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
                userService.delete(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Usuario eliminado correctamente.", null));
        }

}
