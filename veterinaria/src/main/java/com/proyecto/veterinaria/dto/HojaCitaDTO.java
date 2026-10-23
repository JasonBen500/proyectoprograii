package com.proyecto.veterinaria.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data 
public class HojaCitaDTO {
    private Integer idCita;
    private Date fecha;
    private String motivo;
    private String nombreDueno;
    private String telefonoDueno;
    private String nombreMascota;
    private String especieMascota;
    private String nombreMedico;
    private String especialidadMedico;
    private String sintomas;
    private String diagnostico;
    private List<HojaLineaDTO> lineas;
    private BigDecimal total;
}
