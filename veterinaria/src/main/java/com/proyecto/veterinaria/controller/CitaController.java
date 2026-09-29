package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.CitaDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.service.CitaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/citas")
@CrossOrigin (origins = "http://localhost:5173/")
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
    public ResponseEntity<MessageResponse> agregar(@Valid @RequestBody CitaDTO dto) {
        try {
            citaService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Cita creada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al crear la cita"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@Valid @PathVariable Integer id, @RequestBody CitaDTO dto) {
        try {
            citaService.modificar(id, dto);
            return ResponseEntity.ok(new MessageResponse("Cita actualizada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al actualizar la cita"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        try {
            citaService.eliminar(id);
            return ResponseEntity.ok(new MessageResponse("Cita eliminada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al eliminar la cita"));
        }
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        try {
            citaService.anular(id);
            return ResponseEntity.ok(new MessageResponse("Cita anulada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al anular la cita"));
        }
    }
}