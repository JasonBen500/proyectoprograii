package com.proyecto.veterinaria.dto;

import lombok.Data;

@Data 
public class ConsultaDTO {
    private Integer idConsulta;
    private String diagnostico;
    private String sintomas;
    private Boolean estado;
    private Integer idCita;
}
