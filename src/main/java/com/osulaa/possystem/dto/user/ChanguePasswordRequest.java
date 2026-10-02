package com.osulaa.possystem.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChanguePasswordRequest {

    @NotBlank(message = "La contraseña actual es obligatoria.")
    private String currentPassword;

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 8, max = 50, message = "La nueva contraseña debe tener entre 8 y 50 caracteres.")
    private String newPassword;
}
