package com.proyecto.veterinaria.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data 
public class DetalleCitaDTO {
    private Integer idDetalleCita;
    private BigDecimal precioAplicado;
    private Boolean estado;
    private Integer idCita;
    private Integer idMedicamento;
    private Integer idTratamiento;
}
