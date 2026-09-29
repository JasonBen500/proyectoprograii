package com.proyecto.veterinaria.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class MedicoDTO {
    private Integer idMedico;

    @NotBlank(message = "El nombre del médico es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido del médico es obligatorio")
    private String apellido;

    @NotBlank(message = "La cédula es obligatoria")
    private String cedula;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    private Boolean estado;

    @NotNull(message = "El usuario es obligatorio")
    private Integer idUsuario;
}
