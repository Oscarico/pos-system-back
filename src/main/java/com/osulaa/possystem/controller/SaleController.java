package com.osulaa.possystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.osulaa.possystem.dto.common.PageResponse;
import com.osulaa.possystem.dto.sale.SaleRequest;
import com.osulaa.possystem.dto.sale.SaleResponse;
import com.osulaa.possystem.payload.ApiResponse;
import com.osulaa.possystem.service.SaleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

        private final SaleService saleService;

        // GET:
        // http://localhost:8080/sales?page=0&size=10&sortBy=dateSale&sortDir=desc&search=Javier
        @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
        @GetMapping
        public ResponseEntity<ApiResponse<PageResponse<SaleResponse>>> getAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "saleDate") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDir,
                        @RequestParam(required = false) String search) {

                PageResponse<SaleResponse> sales = saleService.findAll(page, size, sortBy, sortDir, search);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Lista de ventas obtenida correctamente",
                                                sales));
        }

        // GET: http://localhost:8080/sales/1
        @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<SaleResponse>> getById(
                        @PathVariable Long id) {
                SaleResponse sale = saleService.findById(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Venta encontrada", sale));
        }

        // POST: http://localhost:8080/sales
        @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
        @PostMapping
        public ResponseEntity<ApiResponse<SaleResponse>> save(
                        @Valid @RequestBody SaleRequest request) {
                SaleResponse sale = saleService.save(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(
                                                ApiResponse.success("Venta registrada exitosamente", sale));
        }

        // PATCH: http://localhost:8080/sales/1/cancel
        @PreAuthorize("hasRole('ADMIN')")
        @PatchMapping("/{id}/cancel")
        public ResponseEntity<ApiResponse<SaleResponse>> cancel(
                        @PathVariable Long id) {
                SaleResponse sale = saleService.cancel(id);

                return ResponseEntity.ok(
                                ApiResponse.success("Venta cancelada exitosamente", sale));
        }

}
