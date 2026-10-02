package com.osulaa.possystem.dto.dashboard;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {

    private SalesSummary sales;
    private InventorySummary inventory;
    private List<SalesChartResponse> salesChart;

    @Data
    @Builder
    public static class SalesSummary {

        private Long salesToday;
        private Double totalSalesToday;

        private Long salesThisMonth;
        private Double totalSalesThisMonth;

        private Long completedSales;
        private Long cancelledSales;
    }

    @Data
    @Builder
    public static class InventorySummary {

        private Long totalProducts;
        private Long lowStockProducts;
        private Long outOfStockProducts;
    }

}