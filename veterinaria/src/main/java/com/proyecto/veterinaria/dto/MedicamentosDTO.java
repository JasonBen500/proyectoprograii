package com.proyecto.veterinaria.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data 
public class MedicamentosDTO {
    private Integer idMedicamento;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Boolean estado;
}
