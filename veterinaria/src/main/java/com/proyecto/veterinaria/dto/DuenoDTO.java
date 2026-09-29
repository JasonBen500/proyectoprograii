package com.proyecto.veterinaria.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DuenoDTO {
    private Integer idDueno;
    @NotBlank(message = "El nombre del dueño es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido del dueño es obligatorio")
    private String apellido;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    private String direccion;
    private Boolean estado;
}
