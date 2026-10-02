package com.osulaa.possystem.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.osulaa.possystem.dto.report.ProductSalesReportResponse;
import com.osulaa.possystem.dto.report.SalesReportResponse;
import com.osulaa.possystem.payload.ApiResponse;
import com.osulaa.possystem.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // Get: http://localhost:8080/report/sales?from=2026-07-21&to=2026-07-22
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<SalesReportResponse>> getSalesReport(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {

        SalesReportResponse report = reportService.getSalesReport(from, to);

        return ResponseEntity.ok(
                ApiResponse.success("Sales report retrieved successfully", report));
    }

    // GET: http://localhost:8080/report/products?from=2026-07-21&to=2026-07-22
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductSalesReportResponse>>> getProductSalesReport(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {

        List<ProductSalesReportResponse> report = reportService.getProductSalesReport(from, to);

        return ResponseEntity.ok(
                ApiResponse.success("Product sales report retrieved successfully", report));
    }
}
