package com.proyecto.veterinaria.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TratamientoDTO {
    private Integer idTratamiento;

    @NotBlank(message = "El nombre del tratamiento es obligatorio")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    private Boolean estado;
}
