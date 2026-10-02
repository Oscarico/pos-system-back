package com.osulaa.possystem.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String name;

    @Size(max = 20)
    private String phone;

    @Email(message = "El correo no es válido")
    @Size(max = 100)
    private String email;
}
