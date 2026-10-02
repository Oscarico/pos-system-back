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

import com.osulaa.possystem.dto.report.ProductSalesReportResponse;
import com.osulaa.possystem.dto.report.SalesReportResponse;
import com.osulaa.possystem.security.CustomUserDetailsService;
import com.osulaa.possystem.security.JwtService;
import com.osulaa.possystem.service.ReportService;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getSalesReport_deberiaRegresarReporteDeVentas() throws Exception {
        LocalDate from = LocalDate.of(2026, 7, 21);
        LocalDate to = LocalDate.of(2026, 7, 22);

        SalesReportResponse mockResponse = SalesReportResponse.builder()
                .totalSales(25L)
                .completedSales(22L)
                .cancelledSales(3L)
                .totalRevenue(4500.0)
                .totalDiscount(150.0)
                .averageSale(204.54)
                .build();

        when(reportService.getSalesReport(from, to)).thenReturn(mockResponse);

        mockMvc.perform(get("/reports/sales")
                .param("from", "2026-07-21")
                .param("to", "2026-07-22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sales report retrieved successfully"))
                .andExpect(jsonPath("$.data.totalSales").value(25))
                .andExpect(jsonPath("$.data.completedSales").value(22))
                .andExpect(jsonPath("$.data.cancelledSales").value(3))
                .andExpect(jsonPath("$.data.totalRevenue").value(4500.0))
                .andExpect(jsonPath("$.data.totalDiscount").value(150.0))
                .andExpect(jsonPath("$.data.averageSale").value(204.54));

        verify(reportService).getSalesReport(from, to);
    }

    @Test
    void getProductSalesReport_deberiaRegresarReporteDeVentasPorProducto() throws Exception {
        LocalDate from = LocalDate.of(2026, 7, 21);
        LocalDate to = LocalDate.of(2026, 7, 22);

        ProductSalesReportResponse product1 = ProductSalesReportResponse.builder()
                .productId(1L)
                .productName("Coca Cola 600ml")
                .totalQuantitySold(50L)
                .totalRevenue(900.0)
                .build();

        ProductSalesReportResponse product2 = ProductSalesReportResponse.builder()
                .productId(2L)
                .productName("Galletas Chokis")
                .totalQuantitySold(30L)
                .totalRevenue(600.0)
                .build();

        when(reportService.getProductSalesReport(from, to))
                .thenReturn(List.of(product1, product2));

        mockMvc.perform(get("/reports/products")
                .param("from", "2026-07-21")
                .param("to", "2026-07-22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product sales report retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].productId").value(1))
                .andExpect(jsonPath("$.data[0].productName").value("Coca Cola 600ml"))
                .andExpect(jsonPath("$.data[0].totalQuantitySold").value(50))
                .andExpect(jsonPath("$.data[0].totalRevenue").value(900.0))
                .andExpect(jsonPath("$.data[1].productId").value(2))
                .andExpect(jsonPath("$.data[1].productName").value("Galletas Chokis"));

        verify(reportService).getProductSalesReport(from, to);
    }
}