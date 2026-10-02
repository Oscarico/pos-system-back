package com.osulaa.possystem.dto.sale;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class SaleRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private Long userId;

    @PositiveOrZero
    private Double discount;

    @NotEmpty
    private List<SaleItemRequest> items;

}
