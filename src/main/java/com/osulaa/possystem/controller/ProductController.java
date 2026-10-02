package com.osulaa.possystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.dto.product.ProductRequest;
import com.osulaa.possystem.dto.product.ProductResponse;
import com.osulaa.possystem.payload.ApiResponse;
import com.osulaa.possystem.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

        private final ProductService productService;

        // GET:
        // http://localhost:8080/products?page=0&size=20&sortBy=priceSale&sortDir=desc&search=coca
        @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
        @GetMapping
        public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "name") String sortBy,
                        @RequestParam(defaultValue = "asc") String sortDir,
                        @RequestParam(required = false) String search) {
                PageResponse<ProductResponse> products = productService.findAll(page, size, sortBy, sortDir, search);

                return ResponseEntity.ok(
                                ApiResponse.success("Lista de productos obtenida exitosamente.", products));
        }

        // GET: http://localhost:8080/products/{id}
        @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
                ProductResponse product = productService.findById(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Producto encontrado.", product));
        }

        // POST: http://localhost:8080/products
        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ResponseEntity<ApiResponse<ProductResponse>> save(@Valid @RequestBody ProductRequest request) {
                ProductResponse saved = productService.save(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success("Producto creado correctamente.", saved));
        }

        // PUT: http://localhost:8080/products/{id}
        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id,
                        @Valid @RequestBody ProductRequest request) {
                ProductResponse updated = productService.update(id, request);

                return ResponseEntity.ok(
                                ApiResponse.success("Producto actualizado correctamente.", updated));
        }

        // DELETE: http://localhost:8080/products/{id}
        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
                productService.delete(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Producto eliminado correctamente.", null));
        }
}
