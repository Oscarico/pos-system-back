package com.osulaa.possystem.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.osulaa.possystem.dto.dashboard.DashboardResponse;
import com.osulaa.possystem.dto.dashboard.SalesChartResponse;
import com.osulaa.possystem.security.CustomUserDetailsService;
import com.osulaa.possystem.security.JwtService;
import com.osulaa.possystem.service.DashboardService;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getSummary_deberiaRegresarDashboardSummary() throws Exception {
        // Arrange
        DashboardResponse.SalesSummary sales = DashboardResponse.SalesSummary.builder()
                .salesToday(5L)
                .totalSalesToday(750.0)
                .salesThisMonth(20L)
                .totalSalesThisMonth(3500.0)
                .completedSales(20L)
                .cancelledSales(3L)
                .build();

        DashboardResponse.InventorySummary inventory = DashboardResponse.InventorySummary.builder()
                .totalProducts(100L)
                .lowStockProducts(8L)
                .outOfStockProducts(2L)
                .build();

        SalesChartResponse point1 = SalesChartResponse.builder()
                .date(LocalDate.now())
                .total(500.0)
                .build();

        SalesChartResponse point2 = SalesChartResponse.builder()
                .date(LocalDate.now().minusDays(1))
                .total(250.0)
                .build();

        DashboardResponse mockResponse = DashboardResponse.builder()
                .sales(sales)
                .inventory(inventory)
                .salesChart(List.of(point1, point2))
                .build();

        when(dashboardService.getSummary()).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Dashboard data retrieved successfully"))
                .andExpect(jsonPath("$.data.sales.salesToday").value(5))
                .andExpect(jsonPath("$.data.sales.totalSalesToday").value(750.0))
                .andExpect(jsonPath("$.data.sales.salesThisMonth").value(20))
                .andExpect(jsonPath("$.data.sales.totalSalesThisMonth").value(3500.0))
                .andExpect(jsonPath("$.data.sales.completedSales").value(20))
                .andExpect(jsonPath("$.data.sales.cancelledSales").value(3))
                .andExpect(jsonPath("$.data.inventory.totalProducts").value(100))
                .andExpect(jsonPath("$.data.inventory.lowStockProducts").value(8))
                .andExpect(jsonPath("$.data.inventory.outOfStockProducts").value(2))
                .andExpect(jsonPath("$.data.salesChart.length()").value(2))
                .andExpect(jsonPath("$.data.salesChart[0].total").value(500.0))
                .andExpect(jsonPath("$.data.salesChart[1].total").value(250.0));

        verify(dashboardService).getSummary();
    }
}