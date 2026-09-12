package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.CitaDTO;
import com.proyecto.veterinaria.service.CitaService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;

    @GetMapping("/activas")
    public List<CitaDTO> mostrarActivas() {
    return citaService.filtroActivas();
    }

    @GetMapping("/mascota/{idMascota}")
    public List<CitaDTO> porMascota(@PathVariable Integer idMascota) {
    return citaService.filtroMascota(idMascota);
    }

    @GetMapping("/medico/{idMedico}")
    public List<CitaDTO> porMedico(@PathVariable Integer idMedico) {
    return citaService.filtroMedico(idMedico);
    }

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public List<CitaDTO> mostrarTodas() {
        return citaService.findAll();
    }

    @GetMapping("/{id}")
    public CitaDTO mostrarUna(@PathVariable Integer id) {
        return citaService.findById(id);
    }

    @PostMapping
    public CitaDTO agregar(@RequestBody CitaDTO dto) {
        return citaService.crear(dto);
    }

    @PutMapping("/{id}")
    public CitaDTO modificar(@PathVariable Integer id, @RequestBody CitaDTO dto) {
        return citaService.modificar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        citaService.eliminar(id);
    }

    @PatchMapping("/{id}/anular")
    public CitaDTO anular(@PathVariable Integer id) {
        return citaService.anular(id);
    }
}