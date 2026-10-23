package com.proyecto.veterinaria.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class HojaLineaDTO {
    private String tipo;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
