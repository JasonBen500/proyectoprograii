package com.proyecto.veterinaria.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data 
public class MascotaDTO {
    private Integer idMascota;
    private String nombre;
    private String especie;
    private Integer edad;
    private BigDecimal peso;
    private Boolean estado;
    private Integer idDueno;
}
