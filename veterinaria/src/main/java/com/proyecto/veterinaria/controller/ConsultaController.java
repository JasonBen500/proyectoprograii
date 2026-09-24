package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.ConsultaDTO;
import com.proyecto.veterinaria.service.ConsultaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping
    public List<ConsultaDTO> mostrarTodas() {
        return consultaService.findAll();
    }

    @GetMapping("/{id}")
    public ConsultaDTO mostrarUna(@PathVariable Integer id) {
        return consultaService.findById(id);
    }

    @PostMapping
    public ConsultaDTO agregar(@RequestBody ConsultaDTO dto) {
        return consultaService.crear(dto);
    }

    @PutMapping("/{id}")
    public ConsultaDTO modificar(@PathVariable Integer id, @RequestBody ConsultaDTO dto) {
        return consultaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        consultaService.eliminar(id);
    }

    @PatchMapping("/{id}/anular")
    public ConsultaDTO anular(@PathVariable Integer id) {
        return consultaService.anular(id);
    }

    @GetMapping("/activas")
    public List<ConsultaDTO> mostrarActivas() {
        return consultaService.findActivas();
    }

    @GetMapping("/cita/{idCita}")
    public List<ConsultaDTO> porCita(@PathVariable Integer idCita) {
        return consultaService.findByCita(idCita);
    }

    @GetMapping("/buscar")
    public List<ConsultaDTO> buscarPorDiagnostico(@RequestParam String diagnostico) {
        return consultaService.buscarPorDiagnostico(diagnostico);
    }
}
