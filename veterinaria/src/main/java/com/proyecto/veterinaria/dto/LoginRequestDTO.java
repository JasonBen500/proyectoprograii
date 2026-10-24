package com.proyecto.veterinaria.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class LoginRequestDTO {
    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}
