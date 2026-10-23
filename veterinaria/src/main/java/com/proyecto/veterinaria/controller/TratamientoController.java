package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.dto.TratamientoDTO;
import com.proyecto.veterinaria.service.TratamientoService;

import jakarta.validation.Valid;

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
    public ResponseEntity<MessageResponse> agregar(@Valid @RequestBody TratamientoDTO dto) {
        tratamientoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Tratamiento creado con exito"));
    }

    @PutMapping("/{id}")
      public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id, @Valid @RequestBody TratamientoDTO dto) {
        tratamientoService.actualizar(id, dto);
        return ResponseEntity.ok(new MessageResponse("Tratamiento actualizado con exito"));
    }


    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        tratamientoService.anular(id);
        return ResponseEntity.ok(new MessageResponse("Tratamiento anulado con exito"));
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
