package com.osulaa.possystem.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Código de barras es requerido.")
    @Size(max = 50, message = "El código de barras debe tener menos de 50 caracteres.")
    private String barcode;

    @NotBlank(message = "Nombre es requerido.")
    @Size(max = 100, message = "El nombre debe tener menos de 100 caracteres.")
    private String name;

    @Size(max = 255, message = "La descripción debe tener menos de 255 caracteres.")
    private String description;

    @NotNull
    @Positive
    private Double purchasePrice;

    @NotNull
    @Positive
    private Double sellPrice;

    @PositiveOrZero
    private Integer stock;

    @NotNull(message = "La categoría es requerida.")
    private Long categoryId;
}
