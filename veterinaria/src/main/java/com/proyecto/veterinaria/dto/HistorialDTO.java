package com.proyecto.veterinaria.dto;

import java.util.Date;

import lombok.Data;

@Data 
public class HistorialDTO {
private Integer idHistorial;
private Date fecha;
private String usuario;
private String accion;
private String entidad;
private String descripcion;
}
