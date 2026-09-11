package com.proyecto.veterinaria.dto;

import java.util.Date;

import lombok.Data;

@Data 
public class CitaDTO {
    private Integer idCita;
    private Date fecha;
    private String motivo;
    private Long total;
    private Boolean estado;
    private Integer idMascota;
    private Integer idMedico;
    private Integer idUsuario;
}
