package com.osulaa.possystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.osulaa.possystem.dto.dashboard.DashboardResponse;
import com.osulaa.possystem.enums.SaleStatus;
import com.osulaa.possystem.repository.ProductRepository;
import com.osulaa.possystem.repository.SaleRepository;
import com.osulaa.possystem.repository.projection.SalesChartProjection;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SalesChartProjection chartProjection;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        // No necesitamos datos globales por ahora.
    }

    @Test
    void getSummary_deberiaRegresarResumenDelDashboard() {

        // Ventas de hoy
        when(saleRepository.countByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(5L, 120L);

        when(saleRepository.sumTotalByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(2500.0, 45000.0);

        // Ventas completadas y canceladas
        when(saleRepository.countByStatus(SaleStatus.COMPLETED))
                .thenReturn(120L);

        when(saleRepository.countByStatus(SaleStatus.CANCELLED))
                .thenReturn(8L);

        // Inventario
        when(productRepository.countByActiveTrue())
                .thenReturn(50L);

        when(productRepository.countActiveLowStock(10))
                .thenReturn(7L);

        when(productRepository.countActiveOutOfStock())
                .thenReturn(3L);

        // Gráfica
        when(chartProjection.getDate())
                .thenReturn(LocalDate.of(2026, 8, 10));

        when(chartProjection.getTotal())
                .thenReturn(1500.0);

        when(saleRepository.findSalesChart(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(chartProjection));

        DashboardResponse result = dashboardService.getSummary();

        assertNotNull(result);

        assertNotNull(result.getSales());
        assertNotNull(result.getInventory());
        assertNotNull(result.getSalesChart());

        // Ventas
        assertEquals(5L, result.getSales().getSalesToday());
        assertEquals(2500.0, result.getSales().getTotalSalesToday());

        assertEquals(120L, result.getSales().getSalesThisMonth());
        assertEquals(45000.0, result.getSales().getTotalSalesThisMonth());

        assertEquals(120L, result.getSales().getCompletedSales());
        assertEquals(8L, result.getSales().getCancelledSales());

        // Inventario
        assertEquals(50L, result.getInventory().getTotalProducts());
        assertEquals(7L, result.getInventory().getLowStockProducts());
        assertEquals(3L, result.getInventory().getOutOfStockProducts());

        // Gráfica
        assertEquals(1, result.getSalesChart().size());
        assertEquals(
                LocalDate.of(2026, 8, 10),
                result.getSalesChart().get(0).getDate());

        assertEquals(
                1500.0,
                result.getSalesChart().get(0).getTotal());
    }

    @Test
    void getSummary_deberiaConsultarVentasDeHoy() {

        when(saleRepository.countByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(0L);

        when(saleRepository.sumTotalByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(0.0);

        when(saleRepository.countByStatus(SaleStatus.COMPLETED))
                .thenReturn(0L);

        when(saleRepository.countByStatus(SaleStatus.CANCELLED))
                .thenReturn(0L);

        when(productRepository.countByActiveTrue())
                .thenReturn(0L);

        when(productRepository.countActiveLowStock(10))
                .thenReturn(0L);

        when(productRepository.countActiveOutOfStock())
                .thenReturn(0L);

        when(saleRepository.findSalesChart(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of());

        dashboardService.getSummary();

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowStart = todayStart.plusDays(1);

        verify(saleRepository).countByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                todayStart,
                tomorrowStart);

        verify(saleRepository).sumTotalByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                todayStart,
                tomorrowStart);
    }

    @Test
    void getSummary_deberiaConsultarVentasDelMes() {

        when(saleRepository.countByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(0L);

        when(saleRepository.sumTotalByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(0.0);

        when(saleRepository.countByStatus(SaleStatus.COMPLETED))
                .thenReturn(0L);

        when(saleRepository.countByStatus(SaleStatus.CANCELLED))
                .thenReturn(0L);

        when(productRepository.countByActiveTrue())
                .thenReturn(0L);

        when(productRepository.countActiveLowStock(10))
                .thenReturn(0L);

        when(productRepository.countActiveOutOfStock())
                .thenReturn(0L);

        when(saleRepository.findSalesChart(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of());

        dashboardService.getSummary();

        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

        LocalDateTime monthStart = firstDayOfMonth.atStartOfDay();

        LocalDateTime nextMonthStart = monthStart.plusMonths(1);

        verify(saleRepository).countByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                monthStart,
                nextMonthStart);

        verify(saleRepository).sumTotalByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                monthStart,
                nextMonthStart);
    }

    @Test
    void getSummary_deberiaConsultarVentasCompletadasYCanceladas() {

        when(saleRepository.countByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(0L);

        when(saleRepository.sumTotalByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(0.0);

        when(saleRepository.countByStatus(SaleStatus.COMPLETED))
                .thenReturn(10L);

        when(saleRepository.countByStatus(SaleStatus.CANCELLED))
                .thenReturn(2L);

        when(productRepository.countByActiveTrue())
                .thenReturn(0L);

        when(productRepository.countActiveLowStock(10))
                .thenReturn(0L);

        when(productRepository.countActiveOutOfStock())
                .thenReturn(0L);

        when(saleRepository.findSalesChart(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of());

        DashboardResponse result = dashboardService.getSummary();

        assertEquals(
                10L,
                result.getSales().getCompletedSales());

        assertEquals(
                2L,
                result.getSales().getCancelledSales());

        verify(saleRepository)
                .countByStatus(SaleStatus.COMPLETED);

        verify(saleRepository)
                .countByStatus(SaleStatus.CANCELLED);
    }

    @Test
    void getSummary_deberiaConsultarInventario() {

        when(saleRepository.countByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(0L);

        when(saleRepository.sumTotalByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(0.0);

        when(saleRepository.countByStatus(any(SaleStatus.class)))
                .thenReturn(0L);

        when(productRepository.countByActiveTrue())
                .thenReturn(100L);

        when(productRepository.countActiveLowStock(10))
                .thenReturn(15L);

        when(productRepository.countActiveOutOfStock())
                .thenReturn(5L);

        when(saleRepository.findSalesChart(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of());

        DashboardResponse result = dashboardService.getSummary();

        assertEquals(
                100L,
                result.getInventory().getTotalProducts());

        assertEquals(
                15L,
                result.getInventory().getLowStockProducts());

        assertEquals(
                5L,
                result.getInventory().getOutOfStockProducts());

        verify(productRepository)
                .countByActiveTrue();

        verify(productRepository)
                .countActiveLowStock(10);

        verify(productRepository)
                .countActiveOutOfStock();
    }

    @Test
    void getSummary_deberiaRegresarListaVaciaSiNoHayVentasEnGrafica() {

        when(saleRepository.countByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(0L);

        when(saleRepository.sumTotalByStatusAndSaleDateBetween(
                any(SaleStatus.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(0.0);

        when(saleRepository.countByStatus(any(SaleStatus.class)))
                .thenReturn(0L);

        when(productRepository.countByActiveTrue())
                .thenReturn(0L);

        when(productRepository.countActiveLowStock(10))
                .thenReturn(0L);

        when(productRepository.countActiveOutOfStock())
                .thenReturn(0L);

        when(saleRepository.findSalesChart(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of());

        DashboardResponse result = dashboardService.getSummary();

        assertNotNull(result);
        assertNotNull(result.getSalesChart());
        assertEquals(0, result.getSalesChart().size());

        verify(saleRepository)
                .findSalesChart(
                        any(LocalDateTime.class),
                        any(LocalDateTime.class));
    }
}