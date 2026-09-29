package com.proyecto.veterinaria.dto;

import java.util.Date;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CitaDTO {
    private Integer idCita;

    @NotNull(message = "La fecha es obligatoria")
    private Date fecha;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    @NotNull(message = "El total es obligatorio")
    @Min(value = 0, message = "El total no puede ser negativo")
    private Long total;

    private Boolean estado;

    @NotNull(message = "La mascota es obligatoria")
    private Integer idMascota;

    @NotNull(message = "El médico es obligatorio")
    private Integer idMedico;

    @NotNull(message = "El usuario es obligatorio")
    private Integer idUsuario;
}
