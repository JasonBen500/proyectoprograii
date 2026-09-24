package com.proyecto.veterinaria.dto;

import lombok.Data;

@Data 
public class DuenoDTO {
    private Integer idDueno;
    private String nombre;
    private String apellido;
    private String telefono;
    private String direccion;
    private Boolean estado;
}

  