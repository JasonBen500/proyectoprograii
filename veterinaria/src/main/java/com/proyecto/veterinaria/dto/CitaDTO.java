package com.proyecto.veterinaria.dto;

import java.math.BigDecimal;
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

    // Lo calcula el servidor a partir de los detalles; el valor enviado se ignora
    private BigDecimal total;

    private Boolean estado;

    @NotNull(message = "La mascota es obligatoria")
    private Integer idMascota;

    @NotNull(message = "El médico es obligatorio")
    private Integer idMedico;

    @NotNull(message = "El usuario es obligatorio")
    private Integer idUsuario;
    private String nombreMascota;
    private String nombreMedico;
    private String nombreUsuario;
}
