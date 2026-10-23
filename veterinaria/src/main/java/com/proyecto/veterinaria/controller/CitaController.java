package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.CitaDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.service.CitaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.proyecto.veterinaria.dto.HojaCitaDTO;

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

    @GetMapping("/{id}/hoja")
    public HojaCitaDTO hoja(@PathVariable Integer id) {
        return citaService.hoja(id);
    }

    @PostMapping
    public ResponseEntity<?> agregar(@Valid @RequestBody CitaDTO dto) {
        CitaDTO creado = citaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id, @Valid @RequestBody CitaDTO dto) {
        citaService.modificar(id, dto);
        return ResponseEntity.ok(new MessageResponse("Cita actualizada con exito"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        citaService.eliminar(id);
        return ResponseEntity.ok(new MessageResponse("Cita eliminada con exito"));

    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        citaService.anular(id);
        return ResponseEntity.ok(new MessageResponse("Cita anulada con exito"));
    }
}