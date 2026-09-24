package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.MedicamentosDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.service.MedicamentosService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicamentos")
public class MedicamentosController {

    private final MedicamentosService medicamentosService;

    public MedicamentosController(MedicamentosService medicamentosService) {
        this.medicamentosService = medicamentosService;
    }

    @GetMapping
    public List<MedicamentosDTO> mostrarTodos() {
        return medicamentosService.findAll();
    }

    @GetMapping("/{id}")
    public MedicamentosDTO mostrarUno(@PathVariable Integer id) {
        return medicamentosService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> agregar(@RequestBody MedicamentosDTO dto) {
        try {
            medicamentosService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Medicamento creado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al crear el medicamento"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id, @RequestBody MedicamentosDTO dto) {
        try {
            medicamentosService.actualizar(id, dto);
            return ResponseEntity.ok(new MessageResponse("Medicamento actualizado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al actualizar el medicamento"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        try {
            medicamentosService.eliminar(id);
            return ResponseEntity.ok(new MessageResponse("Medicamento eliminado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al eliminar el medicamento"));
        }
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        try {
            medicamentosService.anular(id);
            return ResponseEntity.ok(new MessageResponse("Medicamento anulado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al anular el medicamento"));
        }
    }

    @GetMapping("/activos")
    public List<MedicamentosDTO> mostrarActivos() {
        return medicamentosService.findActivos();
    }

    @GetMapping("/buscar")
    public List<MedicamentosDTO> buscarPorNombre(@RequestParam String nombre) {
        return medicamentosService.buscarPorNombre(nombre);
    }

    @GetMapping("/stock-bajo/{cantidad}")
    public List<MedicamentosDTO> stockBajo(@PathVariable Integer cantidad) {
        return medicamentosService.findStockBajo(cantidad);
    }
}