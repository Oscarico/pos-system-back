package com.osulaa.possystem.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesReportResponse {

    private Long totalSales;

    private Long completedSales;

    private Long cancelledSales;

    private Double totalRevenue;

    private Double totalDiscount;

    private Double averageSale;

}
