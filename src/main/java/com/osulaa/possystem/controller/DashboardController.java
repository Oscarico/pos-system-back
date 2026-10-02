package com.osulaa.possystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.osulaa.possystem.dto.dashboard.DashboardResponse;
import com.osulaa.possystem.payload.ApiResponse;
import com.osulaa.possystem.service.DashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // GET: http://localhost:8080/dashboard/summary
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardResponse>> getSummary() {

        DashboardResponse dashboard = dashboardService.getSummary();

        return ResponseEntity.ok(
                ApiResponse.success("Dashboard data retrieved successfully", dashboard));
    }

}
