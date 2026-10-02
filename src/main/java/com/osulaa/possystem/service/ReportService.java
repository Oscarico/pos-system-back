package com.osulaa.possystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.osulaa.possystem.dto.report.ProductSalesReportResponse;
import com.osulaa.possystem.dto.report.SalesReportResponse;
import com.osulaa.possystem.enums.SaleStatus;
import com.osulaa.possystem.repository.SaleDetailRepository;
import com.osulaa.possystem.repository.SaleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;

    public SalesReportResponse getSalesReport(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        long completedSales = saleRepository.countByStatusAndSaleDateBetween(SaleStatus.COMPLETED, start, end);

        long cancelledSales = saleRepository.countByStatusAndSaleDateBetween(SaleStatus.CANCELLED, start, end);

        Double totalRevenue = saleRepository.sumTotalByStatusAndSaleDateBetween(SaleStatus.COMPLETED, start, end);

        Double totalDiscount = saleRepository.sumDiscountByStatusAndSaleDateBetween(
                SaleStatus.COMPLETED,
                start,
                end);

        Double averageSale = completedSales > 0 ? totalRevenue / completedSales : 0.0;

        return SalesReportResponse.builder()
                .totalSales(completedSales + cancelledSales)
                .completedSales(completedSales)
                .cancelledSales(cancelledSales)
                .totalRevenue(totalRevenue)
                .totalDiscount(totalDiscount)
                .averageSale(averageSale)
                .build();

    }

    public List<ProductSalesReportResponse> getProductSalesReport(LocalDate from, LocalDate to) {

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        return saleDetailRepository.findProductSalesReport(start, end)
                .stream()
                .map(product -> ProductSalesReportResponse.builder()
                        .productId(product.getProductId())
                        .productName(product.getProductName())
                        .totalQuantitySold(product.getTotalQuantitySold())
                        .totalRevenue(product.getTotalRevenue())
                        .build())
                .toList();

    }
}
