package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.DuenoDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.service.DuenoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/duenos")
public class DuenoController {

    private final DuenoService duenoService;

    public DuenoController(DuenoService duenoService) {
        this.duenoService = duenoService;
    }

    @GetMapping
    public List<DuenoDTO> mostrarTodos() {
        return duenoService.findAll();
    }

    @GetMapping("/{id}")
    public DuenoDTO mostrarUno(@PathVariable Integer id) {
        return duenoService.findById(id);
    }

    @PostMapping
    public ResponseEntity<?> agregar(@Valid @RequestBody DuenoDTO dto) {
        DuenoDTO creado = duenoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id, @Valid @RequestBody DuenoDTO dto) {
        duenoService.actualizar(id, dto);
        return ResponseEntity.ok(new MessageResponse("Dueno actualizado con exito"));
    }

    @PatchMapping("/anular/{id}")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        duenoService.anular(id);
        return ResponseEntity.ok(new MessageResponse("Dueno anulado con exito"));
    }

    @GetMapping("/activos")
    public List<DuenoDTO> mostrarActivos() {
        return duenoService.findActivos();
    }

    @GetMapping("/buscar")
    public List<DuenoDTO> buscar(@RequestParam String texto) {
        return duenoService.buscarPorNombreOApellido(texto);
    }

    @GetMapping("/existe-telefono/{telefono}")
    public boolean existeTelefono(@PathVariable String telefono) {
        return duenoService.existeTelefono(telefono);
    }
}
