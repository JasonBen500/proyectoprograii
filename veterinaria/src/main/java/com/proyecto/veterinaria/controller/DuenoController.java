package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.DuenoDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.service.DuenoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dueños")
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
    public ResponseEntity<MessageResponse> agregar(@RequestBody DuenoDTO dto) {
        try {
            duenoService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Dueno creado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al crear el dueno"));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id, @RequestBody DuenoDTO dto) {
        try {
            duenoService.actualizar(id, dto);
            return ResponseEntity.ok(new MessageResponse("Dueno actualizado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al actualizar el dueno"));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        try {
            duenoService.eliminar(id);
            return ResponseEntity.ok(new MessageResponse("Dueno eliminado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al eliminar el dueno"));
        }
    }

    @PatchMapping("/anular/{id}")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        try {
            duenoService.anular(id);
            return ResponseEntity.ok(new MessageResponse("Dueno anulado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al anular el dueno"));
        }
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
