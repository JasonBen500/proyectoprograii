package com.proyecto.veterinaria.dto;

import lombok.Data;

@Data 
public class LoginResponseDTO {
    private Integer idUsuario;
    private String nombre;
    private String usuario;
    private String correo;
    private Integer idPerfil;
    private String nombrePerfil;
    private String token;
}
