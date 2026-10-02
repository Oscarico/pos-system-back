package com.osulaa.possystem.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotNull
    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres.")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, max = 50, message = "La contraseña debe tener entre 8 y 50 caracteres.")
    private String password;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres.")
    private String name;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(min = 3, max = 50, message = "El apellido debe tener entre 3 y 50 caracteres.")
    private String lastName;

    @NotNull(message = "El rol es requerido.")
    private Long roleId;

}
