package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.LoginRequestDTO;
import com.proyecto.veterinaria.dto.LoginResponseDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.dto.UsuariosDTO;
import com.proyecto.veterinaria.service.UsuariosService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
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
        usuariosService.agregar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Usuario creado con exito"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id, @Valid @RequestBody UsuariosDTO dto) {
        usuariosService.modificar(id, dto);
        return ResponseEntity.ok(new MessageResponse("Usuario actualizado con exito"));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        usuariosService.anular(id);
        return ResponseEntity.ok(new MessageResponse("Usuario anulado con exito"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto, HttpServletRequest request) {
        LoginResponseDTO respuesta = usuariosService.login(dto, request.getRemoteAddr());
        return ResponseEntity.ok(respuesta);
    }
}
