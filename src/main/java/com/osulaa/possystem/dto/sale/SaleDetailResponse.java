package com.osulaa.possystem.dto.sale;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaleDetailResponse {

    private Long id;

    private Long productId;

    private String productName;

    private Integer quantity;

    private Double unitPrice;

    private Double subtotal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
