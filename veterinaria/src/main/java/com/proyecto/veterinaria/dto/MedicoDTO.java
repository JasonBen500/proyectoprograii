package com.proyecto.veterinaria.dto;

import lombok.Data;

@Data 
public class MedicoDTO {
    private Integer idMedico;
    private String nombre;
    private String apellido;
    private String cedula;
    private String especialidad;
    private Boolean estado;
    private Integer idUsuario;
}
