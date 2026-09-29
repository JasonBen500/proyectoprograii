package com.proyecto.veterinaria.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UsuariosDTO {
    private Integer idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    private Boolean estado;

    @NotNull(message = "El perfil es obligatorio")
    private Integer idPerfil;
}
