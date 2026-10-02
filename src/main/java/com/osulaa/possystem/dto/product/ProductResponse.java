package com.osulaa.possystem.dto.product;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {

    private Long id;

    private String barcode;

    private String name;

    private String description;

    private Double purchasePrice;

    private Double sellPrice;

    private Integer stock;

    private Long categoryId;

    private String categoryName;

    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
