package com.proyecto.veterinaria.dto;

public class MessageResponse {
    private String mensaje;

    public MessageResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}