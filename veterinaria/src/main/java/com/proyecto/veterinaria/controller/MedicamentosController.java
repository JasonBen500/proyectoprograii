package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.MedicamentosDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.service.MedicamentosService;

import jakarta.validation.Valid;

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
    public ResponseEntity<MessageResponse> agregar(@Valid @RequestBody MedicamentosDTO dto) {
        medicamentosService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Medicamento creado con exito"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id,
            @Valid @RequestBody MedicamentosDTO dto) {
        medicamentosService.actualizar(id, dto);
        return ResponseEntity.ok(new MessageResponse("Medicamento actualizado con exito"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        medicamentosService.eliminar(id);
        return ResponseEntity.ok(new MessageResponse("Medicamento eliminado con exito"));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        medicamentosService.anular(id);
        return ResponseEntity.ok(new MessageResponse("Medicamento anulado con exito"));
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

    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> ajustarStock(@PathVariable Integer id, @RequestParam Integer cambio) {
        return ResponseEntity.ok(medicamentosService.ajustarStock(id, cambio));
    }
}