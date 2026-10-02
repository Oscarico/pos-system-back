package com.osulaa.possystem.controller;

import java.util.List;

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

import com.osulaa.possystem.dto.category.CategoryRequest;
import com.osulaa.possystem.dto.category.CategoryResponse;
import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.entity.Category;
import com.osulaa.possystem.payload.ApiResponse;
import com.osulaa.possystem.repository.CategoryRepository;
import com.osulaa.possystem.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

        private final CategoryService categoryService;
        private final CategoryRepository categoryRepository;

        // GET:
        // http://localhost:8080/categories?page=0&size=20&sortBy=name&sortDir=desc&search=bebidas
        @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
        @GetMapping
        public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "name") String sortBy,
                        @RequestParam(defaultValue = "asc") String sortDir,
                        @RequestParam(required = false) String search) {
                PageResponse<CategoryResponse> categories = categoryService.findAll(page, size, sortBy, sortDir,
                                search);

                return ResponseEntity.ok(
                                ApiResponse.success("Lista de categorías obtenida correctamente.", categories));
        }

        // GET: http://localhost:8080/categories/{id}
        @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
                CategoryResponse category = categoryService.findById(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Categoría encontrada.", category));
        }

        // POST: http://localhost:8080/categories
        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ResponseEntity<ApiResponse<CategoryResponse>> save(@Valid @RequestBody CategoryRequest request) {
                CategoryResponse saved = categoryService.save(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success("Categoría creada correctamente.", saved));
        }

        // PUT: http://localhost:8080/categories/{id}
        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long id,
                        @Valid @RequestBody CategoryRequest request) {
                CategoryResponse updated = categoryService.update(id, request);

                return ResponseEntity.ok(
                                ApiResponse.success("Categoría actualizada correctamente.", updated));
        }

        // DELETE: http://localhost:8080/categories/{id}
        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
                categoryService.delete(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Categoría eliminada correctamente.", null));
        }

        // GET: http://localhost:8080/categories/all
        @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
        @GetMapping("/all")
        public List<Category> test() {
                return categoryRepository.findAllByActiveTrue();
        }
}
