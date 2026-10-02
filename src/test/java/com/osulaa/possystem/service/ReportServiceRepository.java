package com.osulaa.possystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.osulaa.possystem.dto.report.ProductSalesReportResponse;
import com.osulaa.possystem.dto.report.SalesReportResponse;
import com.osulaa.possystem.enums.SaleStatus;
import com.osulaa.possystem.repository.SaleDetailRepository;
import com.osulaa.possystem.repository.SaleRepository;
import com.osulaa.possystem.repository.projection.ProductSalesProjection;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleDetailRepository saleDetailRepository;

    @Mock
    private ProductSalesProjection productSalesProjection;

    @InjectMocks
    private ReportService reportService;

    @Test
    void getSalesReport_deberiaRegresarReporteDeVentas() {

        LocalDate from = LocalDate.of(2026, 8, 1);
        LocalDate to = LocalDate.of(2026, 8, 10);

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        when(saleRepository.countByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                start,
                end))
                .thenReturn(8L);

        when(saleRepository.countByStatusAndSaleDateBetween(
                SaleStatus.CANCELLED,
                start,
                end))
                .thenReturn(2L);

        when(saleRepository.sumTotalByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                start,
                end))
                .thenReturn(4000.0);

        when(saleRepository.sumDiscountByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                start,
                end))
                .thenReturn(200.0);

        SalesReportResponse result = reportService.getSalesReport(from, to);

        assertNotNull(result);
        assertEquals(10L, result.getTotalSales());
        assertEquals(8L, result.getCompletedSales());
        assertEquals(2L, result.getCancelledSales());
        assertEquals(4000.0, result.getTotalRevenue());
        assertEquals(200.0, result.getTotalDiscount());
        assertEquals(500.0, result.getAverageSale());

        verify(saleRepository).countByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                start,
                end);

        verify(saleRepository).countByStatusAndSaleDateBetween(
                SaleStatus.CANCELLED,
                start,
                end);

        verify(saleRepository).sumTotalByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                start,
                end);

        verify(saleRepository).sumDiscountByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                start,
                end);
    }

    @Test
    void getSalesReport_deberiaRegresarPromedioCeroSiNoHayVentas() {

        LocalDate from = LocalDate.of(2026, 8, 1);
        LocalDate to = LocalDate.of(2026, 8, 10);

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        when(saleRepository.countByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                start,
                end))
                .thenReturn(0L);

        when(saleRepository.countByStatusAndSaleDateBetween(
                SaleStatus.CANCELLED,
                start,
                end))
                .thenReturn(0L);

        when(saleRepository.sumTotalByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                start,
                end))
                .thenReturn(0.0);

        when(saleRepository.sumDiscountByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                start,
                end))
                .thenReturn(0.0);

        SalesReportResponse result = reportService.getSalesReport(from, to);

        assertNotNull(result);
        assertEquals(0L, result.getTotalSales());
        assertEquals(0L, result.getCompletedSales());
        assertEquals(0L, result.getCancelledSales());
        assertEquals(0.0, result.getTotalRevenue());
        assertEquals(0.0, result.getTotalDiscount());
        assertEquals(0.0, result.getAverageSale());
    }

    @Test
    void getProductSalesReport_deberiaRegresarProductosVendidos() {

        LocalDate from = LocalDate.of(2026, 8, 1);
        LocalDate to = LocalDate.of(2026, 8, 10);

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        when(productSalesProjection.getProductId())
                .thenReturn(1L);

        when(productSalesProjection.getProductName())
                .thenReturn("Coca Cola");

        when(productSalesProjection.getTotalQuantitySold())
                .thenReturn(15L);

        when(productSalesProjection.getTotalRevenue())
                .thenReturn(450.0);

        when(saleDetailRepository.findProductSalesReport(start, end))
                .thenReturn(List.of(productSalesProjection));

        List<ProductSalesReportResponse> result = reportService.getProductSalesReport(from, to);

        assertNotNull(result);
        assertEquals(1, result.size());

        ProductSalesReportResponse response = result.get(0);

        assertEquals(1L, response.getProductId());
        assertEquals("Coca Cola", response.getProductName());
        assertEquals(15L, response.getTotalQuantitySold());
        assertEquals(450.0, response.getTotalRevenue());

        verify(saleDetailRepository).findProductSalesReport(start, end);
    }

    @Test
    void getProductSalesReport_deberiaRegresarListaVaciaSiNoHayVentas() {

        LocalDate from = LocalDate.of(2026, 8, 1);
        LocalDate to = LocalDate.of(2026, 8, 10);

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        when(saleDetailRepository.findProductSalesReport(start, end))
                .thenReturn(List.of());

        List<ProductSalesReportResponse> result = reportService.getProductSalesReport(from, to);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(saleDetailRepository).findProductSalesReport(start, end);
    }
}