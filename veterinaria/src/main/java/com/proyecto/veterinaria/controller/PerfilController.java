package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.dto.PerfilDTO;
import com.proyecto.veterinaria.service.PerfilService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/perfiles")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public List<PerfilDTO> mostrarTodos() {
        return perfilService.findAll();
    }

    @GetMapping("/{id}")
    public PerfilDTO mostrarUno(@PathVariable Integer id) {
        return perfilService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> agregar(@RequestBody PerfilDTO dto) {
        try {
            perfilService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Perfil creado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al crear el perfil"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id, @RequestBody PerfilDTO dto) {
        try {
            perfilService.actualizar(id, dto);
            return ResponseEntity.ok(new MessageResponse("Perfil actualizado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al actualizar el perfil"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        try {
            perfilService.eliminar(id);
            return ResponseEntity.ok(new MessageResponse("Perfil eliminado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al eliminar el perfil"));
        }
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        try {
            perfilService.anular(id);
            return ResponseEntity.ok(new MessageResponse("Perfil anulado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al anular el perfil"));
        }
    }

    @GetMapping("/activos")
    public List<PerfilDTO> mostrarActivos() {
        return perfilService.findActivos();
    }

    @GetMapping("/nombre/{nombrePerfil}")
    public PerfilDTO porNombre(@PathVariable String nombrePerfil) {
        return perfilService.findByNombre(nombrePerfil);
    }
}