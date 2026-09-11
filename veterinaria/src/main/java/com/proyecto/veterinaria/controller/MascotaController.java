package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.MascotaDTO;
import com.proyecto.veterinaria.service.MascotaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping
    public List<MascotaDTO> mostrarTodas() {
        return mascotaService.findAll();
    }

    @GetMapping("/{id}")
    public MascotaDTO mostrarUna(@PathVariable Integer id) {
        return mascotaService.findById(id);
    }

    @PostMapping
    public MascotaDTO agregar(@RequestBody MascotaDTO dto) {
        return mascotaService.agregar(dto);
    }

    @PutMapping("/{id}")
    public MascotaDTO modificar(@PathVariable Integer id, @RequestBody MascotaDTO dto) {
        return mascotaService.modificar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        mascotaService.eliminar(id);
    }

    @PatchMapping("/{id}/anular")
    public MascotaDTO anular(@PathVariable Integer id) {
        return mascotaService.anular(id);
    }
}
