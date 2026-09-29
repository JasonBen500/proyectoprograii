package com.proyecto.veterinaria.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class PerfilDTO {
    private Integer idPerfil;

    @NotBlank(message = "El nombre del perfil es obligatorio")
    private String nombrePerfil;

    private Boolean estado;
}
