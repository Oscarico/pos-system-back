package com.osulaa.possystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.osulaa.possystem.dto.client.ClientRequest;
import com.osulaa.possystem.dto.client.ClientResponse;
import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.payload.ApiResponse;
import com.osulaa.possystem.service.ClientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

        private final ClientService clientService;

        // GET:
        // http://localhost/8080/clients?page=0&size=20&sortBy=name&sortDir=desc&search=Juan
        @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
        @GetMapping
        public ResponseEntity<ApiResponse<PageResponse<ClientResponse>>> findAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "name") String sortBy,
                        @RequestParam(defaultValue = "asc") String sortDir,
                        @RequestParam(required = false) String search) {

                PageResponse<ClientResponse> clients = clientService.findAll(page, size, sortBy, sortDir, search);

                return ResponseEntity.ok(
                                ApiResponse.success("Lista de clientes obtenida correctamente.", clients));
        }

        // GET: http://localhost/8080/clients/{id}
        @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<ClientResponse>> getById(@PathVariable Long id) {
                ClientResponse client = clientService.findById(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Cliente encontrado.", client));
        }

        // POST: http://localhost/8080/clients
        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ResponseEntity<ApiResponse<ClientResponse>> save(@Valid @RequestBody ClientRequest request) {
                ClientResponse saved = clientService.save(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success("Cliente creado correctamente.", saved));
        }

        // PUT: http://localhost/8080/clients/{id}
        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<ClientResponse>> update(@PathVariable Long id,
                        @Valid @RequestBody ClientRequest request) {
                ClientResponse updated = clientService.update(id, request);

                return ResponseEntity.ok(
                                ApiResponse.success("Cliente actualizado correctamente.", updated));
        }

        // DELETE: http://localhost/8080/clients/{id}
        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
                clientService.delete(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Cliente eliminado correctamente.", null));
        }

}
