package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.MascotaDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.service.MascotaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping("/activas")
    public List<MascotaDTO> mostrarActivas() {
        return mascotaService.filtroActivas();
    }

    @GetMapping("/dueno/{idDueno}")
    public List<MascotaDTO> porDueno(@PathVariable Integer idDueno) {
        return mascotaService.filtroIdDueno(idDueno);
    }

    @GetMapping("/especie/{especie}")
    public List<MascotaDTO> porEspecie(@PathVariable String especie) {
        return mascotaService.filtroEspecie(especie);
    }

    @GetMapping("/buscar")
    public List<MascotaDTO> buscarPorNombre(@RequestParam String nombre) {
        return mascotaService.filtroNombre(nombre);
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
    public ResponseEntity<?> agregar(@Valid @RequestBody MascotaDTO dto) {
        MascotaDTO creado = mascotaService.agregar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id, @Valid @RequestBody MascotaDTO dto) {
        mascotaService.modificar(id, dto);
        return ResponseEntity.ok(new MessageResponse("Mascota actualizada con exito"));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        mascotaService.anular(id);
        return ResponseEntity.ok(new MessageResponse("Mascota anulada con exito"));
    }
}
