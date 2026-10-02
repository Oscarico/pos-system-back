package com.osulaa.possystem.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSalesReportResponse {

    private Long productId;

    private String productName;

    private Long totalQuantitySold;

    private Double totalRevenue;

}
