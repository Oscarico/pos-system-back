package com.osulaa.possystem.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.osulaa.possystem.dto.sale.SaleResponse;
import com.osulaa.possystem.entity.Sale;

@Component
public class SaleMapper {

    public SaleResponse toResponse(Sale sale) {
        if (sale == null) {
            return null;
        }

        return SaleResponse.builder()
                .id(sale.getId())
                .folio(sale.getFolio())
                .saleDate(sale.getSaleDate())
                .clientName(sale.getClient().getName())
                .username(sale.getUser().getUsername())
                .subtotal(sale.getSubtotal())
                .discount(sale.getDiscount())
                .total(sale.getTotal())
                .status(sale.getStatus())
                .createdAt(sale.getCreatedAt())
                .updatedAt(sale.getUpdatedAt())
                .build();
    }

    public List<SaleResponse> toResponseList(List<Sale> sales) {
        return sales.stream()
                .map(this::toResponse)
                .toList();
    }

}
