package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.MedicoDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.service.MedicoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
@CrossOrigin (origins = "http://localhost:5173/")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping("/activos")
    public List<MedicoDTO> mostrarActivos() {
        return medicoService.filtroActivos();
    }

    @GetMapping("/especialidad/{especialidad}")
    public List<MedicoDTO> porEspecialidad(@PathVariable String especialidad) {
        return medicoService.filtroEspecialidad(especialidad);
    }

    @GetMapping("/cedula/{cedula}")
    public boolean existeCedula(@PathVariable String cedula) {
        return medicoService.Cedula(cedula);
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
    public ResponseEntity<MessageResponse> agregar(@Valid @RequestBody MedicoDTO dto) {
        try {
            medicoService.agregar(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Medico creado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al crear el medico"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@Valid @PathVariable Integer id, @RequestBody MedicoDTO dto) {
        try {
            medicoService.modificar(id, dto);
            return ResponseEntity.ok(new MessageResponse("Medico actualizado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al actualizar el medico"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        try {
            medicoService.eliminar(id);
            return ResponseEntity.ok(new MessageResponse("Medico eliminado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al eliminar el medico"));
        }
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        try {
            medicoService.anular(id);
            return ResponseEntity.ok(new MessageResponse("Medico anulado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al anular el medico"));
        }
    }
}
