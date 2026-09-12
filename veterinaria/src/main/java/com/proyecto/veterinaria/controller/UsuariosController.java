package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.UsuariosDTO;
import com.proyecto.veterinaria.service.UsuariosService;
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
    public UsuariosDTO agregar(@RequestBody UsuariosDTO dto) {
        return usuariosService.agregar(dto);
    }

    @PutMapping("/{id}")
    public UsuariosDTO modificar(@PathVariable Integer id, @RequestBody UsuariosDTO dto) {
        return usuariosService.modificar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        usuariosService.eliminar(id);
    }

    @PatchMapping("/{id}/anular")
    public UsuariosDTO anular(@PathVariable Integer id) {
        return usuariosService.anular(id);
    }
}
