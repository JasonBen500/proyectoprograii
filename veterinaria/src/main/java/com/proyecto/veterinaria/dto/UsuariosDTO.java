package com.proyecto.veterinaria.dto;

import lombok.Data;

@Data 
public class UsuariosDTO {
    private Integer idUsuario;
    private String nombre;
    private String usuario;
    private String contrasena;
    private String correo;
    private Boolean estado;
    private Integer idPerfil;
}
