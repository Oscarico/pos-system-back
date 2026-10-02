package com.osulaa.possystem.dto.sale;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.osulaa.possystem.enums.SaleStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaleResponse {

    private Long id;

    private String folio;

    private LocalDateTime saleDate;

    private String clientName;

    private String username;

    private Double subtotal;

    private Double discount;

    private Double total;

    private SaleStatus status;

    private List<SaleDetailResponse> items;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}
