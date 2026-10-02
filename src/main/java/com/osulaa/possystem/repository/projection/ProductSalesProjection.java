package com.osulaa.possystem.repository.projection;

public interface ProductSalesProjection {

    Long getProductId();

    String getProductName();

    Long getTotalQuantitySold();

    Double getTotalRevenue();

}
