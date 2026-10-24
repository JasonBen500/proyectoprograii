package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.HistorialDTO;
import com.proyecto.veterinaria.service.HistorialService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/historial")
public class HistorialController {

    private final HistorialService historialService;

    public HistorialController(HistorialService historialService) {
        this.historialService = historialService;
    }

    @GetMapping
    public List<HistorialDTO> mostrarRecientes() {
        return historialService.recientes();
    }

    @GetMapping("/ultimos")
    public List<HistorialDTO> mostrarUltimos() {
        return historialService.ultimos();
    }
}
