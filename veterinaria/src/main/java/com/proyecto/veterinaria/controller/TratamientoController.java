package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.dto.TratamientoDTO;
import com.proyecto.veterinaria.service.TratamientoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tratamientos")
public class TratamientoController {

    private final TratamientoService tratamientoService;

    public TratamientoController(TratamientoService tratamientoService) {
        this.tratamientoService = tratamientoService;
    }

    @GetMapping
    public List<TratamientoDTO> mostrarTodos() {
        return tratamientoService.findAll();
    }

    @GetMapping("/{id}")
    public TratamientoDTO mostrarUno(@PathVariable Integer id) {
        return tratamientoService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> agregar(@RequestBody TratamientoDTO dto) {
        try {
            tratamientoService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Tratamiento creado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al crear el tratamiento"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id, @RequestBody TratamientoDTO dto) {
        try {
            tratamientoService.actualizar(id, dto);
            return ResponseEntity.ok(new MessageResponse("Tratamiento actualizado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al actualizar el tratamiento"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        try {
            tratamientoService.eliminar(id);
            return ResponseEntity.ok(new MessageResponse("Tratamiento eliminado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al eliminar el tratamiento"));
        }
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        try {
            tratamientoService.anular(id);
            return ResponseEntity.ok(new MessageResponse("Tratamiento anulado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al anular el tratamiento"));
        }
    }

    @GetMapping("/activos")
    public List<TratamientoDTO> mostrarActivos() {
        return tratamientoService.findActivos();
    }

    @GetMapping("/buscar")
    public List<TratamientoDTO> buscarPorNombre(@RequestParam String nombre) {
        return tratamientoService.buscarPorNombre(nombre);
    }
}