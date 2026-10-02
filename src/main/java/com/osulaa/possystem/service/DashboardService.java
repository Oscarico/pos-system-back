package com.osulaa.possystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.osulaa.possystem.dto.dashboard.DashboardResponse;
import com.osulaa.possystem.dto.dashboard.SalesChartResponse;
import com.osulaa.possystem.enums.SaleStatus;
import com.osulaa.possystem.repository.ProductRepository;
import com.osulaa.possystem.repository.SaleRepository;
import com.osulaa.possystem.repository.projection.SalesChartProjection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public DashboardResponse getSummary() {

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowStart = todayStart.plusDays(1);

        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

        LocalDateTime monthStart = firstDayOfMonth.atStartOfDay();
        LocalDateTime nextMonthStart = monthStart.plusMonths(1);

        // Ventas de hoy
        Long salesToday = saleRepository.countByStatusAndSaleDateBetween(SaleStatus.COMPLETED, todayStart,
                tomorrowStart);
        Double totalSalesToday = saleRepository.sumTotalByStatusAndSaleDateBetween(SaleStatus.COMPLETED, todayStart,
                tomorrowStart);

        // Ventas del mes
        Long salesThisMonth = saleRepository.countByStatusAndSaleDateBetween(SaleStatus.COMPLETED, monthStart,
                nextMonthStart);
        Double totalSalesThisMonth = saleRepository.sumTotalByStatusAndSaleDateBetween(SaleStatus.COMPLETED, monthStart,
                nextMonthStart);

        // Ventas completas y canceladas
        Long completedSales = saleRepository.countByStatus(SaleStatus.COMPLETED);
        Long cancelledSales = saleRepository.countByStatus(SaleStatus.CANCELLED);

        // Total de productos
        Long totalProducts = productRepository.countByActiveTrue();

        Long lowStockProducts = productRepository.countActiveLowStock(10);

        Long outOfStockProducts = productRepository.countActiveOutOfStock();

        LocalDateTime chartStartDate = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDateTime chartEndDate = LocalDate.now().plusDays(1).atStartOfDay();

        List<SalesChartProjection> salesChart = saleRepository.findSalesChart(chartStartDate, chartEndDate);

        List<SalesChartResponse> salesChartResponse = salesChart.stream()
                .map(item -> SalesChartResponse.builder()
                        .date(item.getDate())
                        .total(item.getTotal())
                        .build())
                .toList();

        return DashboardResponse.builder()
                .sales(
                        DashboardResponse.SalesSummary.builder()
                                .salesToday(salesToday)
                                .totalSalesToday(totalSalesToday)
                                .salesThisMonth(salesThisMonth)
                                .totalSalesThisMonth(totalSalesThisMonth)
                                .completedSales(completedSales)
                                .cancelledSales(cancelledSales)
                                .build())
                .inventory(
                        DashboardResponse.InventorySummary.builder()
                                .totalProducts(totalProducts)
                                .lowStockProducts(lowStockProducts)
                                .outOfStockProducts(outOfStockProducts)
                                .build())
                .salesChart(
                        salesChartResponse)
                .build();

    }

}
