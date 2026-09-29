package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.dto.UsuariosDTO;
import com.proyecto.veterinaria.service.UsuariosService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin (origins = "http://localhost:5173/")
public class UsuariosController {

    private final UsuariosService usuariosService;

    public UsuariosController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    @GetMapping("/activos")
    public List<UsuariosDTO> mostrarActivos() {
        return usuariosService.filtroActivos();
    }

    @GetMapping("/existeUsuario/{usuario}")
    public boolean existeUsuario(@PathVariable String usuario) {
        return usuariosService.existeUsuario(usuario);
    }

    @GetMapping("/existeCorreo/{correo}")
    public boolean existeCorreo(@PathVariable String correo) {
        return usuariosService.existeCorreo(correo);
    }

    @GetMapping
    public List<UsuariosDTO> mostrarTodos() {
        return usuariosService.findAll();
    }

    @GetMapping("/{id}")
    public UsuariosDTO mostrarUno(@PathVariable Integer id) {
        return usuariosService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> agregar(@Valid @RequestBody UsuariosDTO dto) {
        try {
            usuariosService.agregar(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Usuario creado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al crear el usuario"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@Valid @PathVariable Integer id, @RequestBody UsuariosDTO dto) {
        try {
            usuariosService.modificar(id, dto);
            return ResponseEntity.ok(new MessageResponse("Usuario actualizado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al actualizar el usuario"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        try {
            usuariosService.eliminar(id);
            return ResponseEntity.ok(new MessageResponse("Usuario eliminado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al eliminar el usuario"));
        }
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        try {
            usuariosService.anular(id);
            return ResponseEntity.ok(new MessageResponse("Usuario anulado con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al anular el usuario"));
        }
    }
}
