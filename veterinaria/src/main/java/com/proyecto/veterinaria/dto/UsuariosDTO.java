package com.proyecto.veterinaria.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class UsuariosDTO {
    private Integer idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    private Boolean estado;

    @NotNull(message = "El perfil es obligatorio")
    private Integer idPerfil;

    private String nombrePerfil;
}
