package com.proyecto.veterinaria.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TratamientoDTO {
    private Integer idTratamiento;
    private String nombre;
    private BigDecimal precio;
    private Boolean estado;
}
