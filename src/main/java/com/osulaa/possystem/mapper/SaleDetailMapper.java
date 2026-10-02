package com.osulaa.possystem.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.osulaa.possystem.dto.sale.SaleDetailResponse;
import com.osulaa.possystem.entity.SaleDetail;

@Component
public class SaleDetailMapper {

    public SaleDetailResponse toResponse(SaleDetail saleDetail) {
        if (saleDetail == null) {
            return null;
        }

        return SaleDetailResponse.builder()
                .id(saleDetail.getId())
                .productId(saleDetail.getProduct().getId())
                .productName(saleDetail.getProduct().getName())
                .quantity(saleDetail.getQuantity())
                .unitPrice(saleDetail.getUnitPrice())
                .subtotal(saleDetail.getSubtotal())
                .createdAt(saleDetail.getCreatedAt())
                .updatedAt(saleDetail.getUpdatedAt())
                .build();
    }

    public List<SaleDetailResponse> toResponseList(List<SaleDetail> sales) {
        return sales.stream()
                .map(this::toResponse)
                .toList();
    }

}
