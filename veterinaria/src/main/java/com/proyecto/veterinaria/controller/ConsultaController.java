package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.ConsultaDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.service.ConsultaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MessageResponse> agregar(@RequestBody ConsultaDTO dto) {
        try {
            consultaService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Consulta creada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al crear la consulta"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id, @RequestBody ConsultaDTO dto) {
        try {
            consultaService.actualizar(id, dto);
            return ResponseEntity.ok(new MessageResponse("Consulta actualizada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al actualizar la consulta"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        try {
            consultaService.eliminar(id);
            return ResponseEntity.ok(new MessageResponse("Consulta eliminada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al eliminar la consulta"));
        }
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        try {
            consultaService.anular(id);
            return ResponseEntity.ok(new MessageResponse("Consulta anulada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al anular la consulta"));
        }
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
