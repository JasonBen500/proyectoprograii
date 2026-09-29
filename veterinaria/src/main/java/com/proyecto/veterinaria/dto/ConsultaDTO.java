package com.proyecto.veterinaria.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ConsultaDTO {
    private Integer idConsulta;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    @NotBlank(message = "Los síntomas son obligatorios")
    private String sintomas;

    private Boolean estado;

    @NotNull(message = "La cita es obligatoria")
    private Integer idCita;
}
