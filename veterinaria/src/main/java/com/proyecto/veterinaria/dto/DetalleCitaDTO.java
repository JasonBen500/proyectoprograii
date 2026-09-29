package com.proyecto.veterinaria.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class DetalleCitaDTO {
    private Integer idDetalleCita;
    
    @NotNull(message = "El precio aplicado es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precioAplicado;

    private Boolean estado;

    @NotNull(message = "La cita es obligatoria")
    private Integer idCita;

    private Integer idMedicamento;

    private Integer idTratamiento;
}
