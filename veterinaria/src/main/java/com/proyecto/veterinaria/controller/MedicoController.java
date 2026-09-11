package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.MedicoDTO;
import com.proyecto.veterinaria.service.MedicoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping
    public List<MedicoDTO> mostrarTodos() {
        return medicoService.findAll();
    }

    @GetMapping("/{id}")
    public MedicoDTO mostrarUno(@PathVariable Integer id) {
        return medicoService.findById(id);
    }

    @PostMapping
    public MedicoDTO agregar(@RequestBody MedicoDTO dto) {
        return medicoService.agregar(dto);
    }

    @PutMapping("/{id}")
    public MedicoDTO modificar(@PathVariable Integer id, @RequestBody MedicoDTO dto) {
        return medicoService.modificar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        medicoService.eliminar(id);
    }

    @PatchMapping("/{id}/anular")
    public MedicoDTO anular(@PathVariable Integer id) {
        return medicoService.anular(id);
    }
}
