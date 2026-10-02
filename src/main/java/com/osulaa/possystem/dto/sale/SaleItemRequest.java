package com.osulaa.possystem.dto.sale;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SaleItemRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Integer quantity;

}
