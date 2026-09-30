package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.dto.LoginRequestDTO;
import com.proyecto.veterinaria.dto.LoginResponseDTO;
import com.proyecto.veterinaria.dto.UsuariosDTO;
import com.proyecto.veterinaria.entity.Usuarios;
import com.proyecto.veterinaria.entity.Perfil;
import com.proyecto.veterinaria.repository.UsuariosRepository;
import com.proyecto.veterinaria.security.JwtService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;
    private final JwtService jwtService;

    public UsuariosService(UsuariosRepository usuariosRepository, JwtService jwtService) {
        this.usuariosRepository = usuariosRepository;
        this.jwtService = jwtService;
    }

    public List<UsuariosDTO> filtroActivos() {
        return usuariosRepository.findByEstadoTrueOrderByIdUsuarioDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean existeUsuario(String usuario) {
        return usuariosRepository.existsByUsuario(usuario);
    }

    public boolean existeCorreo(String correo) {
        return usuariosRepository.existsByCorreo(correo);
    }

    public List<UsuariosDTO> findAll() {
        return usuariosRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UsuariosDTO findById(Integer id) {
        Usuarios entity = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDTO(entity);
    }

    public UsuariosDTO agregar(UsuariosDTO dto) {
        Usuarios entity = convertToEntity(dto);
        return convertToDTO(usuariosRepository.save(entity));
    }

    public UsuariosDTO modificar(Integer id, UsuariosDTO dto) {
        Usuarios entity = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        entity.setNombre(dto.getNombre());
        entity.setUsuario(dto.getUsuario());
        entity.setContrasena(dto.getContrasena());
        entity.setCorreo(dto.getCorreo());
        entity.setEstado(dto.getEstado());
        if (dto.getIdPerfil() != null)
            entity.setIdPerfil(new Perfil(dto.getIdPerfil()));

        return convertToDTO(usuariosRepository.save(entity));
    }

    public void eliminar(Integer id) {
        usuariosRepository.deleteById(id);
    }

    public UsuariosDTO anular(Integer id) {
        Usuarios entity = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        entity.setEstado(false);
        return convertToDTO(usuariosRepository.save(entity));
    }

    private Usuarios convertToEntity(UsuariosDTO dto) {
        Usuarios entity = new Usuarios();
        entity.setNombre(dto.getNombre());
        entity.setUsuario(dto.getUsuario());
        entity.setContrasena(dto.getContrasena());
        entity.setCorreo(dto.getCorreo());
        entity.setEstado(dto.getEstado());
        if (dto.getIdPerfil() != null)
            entity.setIdPerfil(new Perfil(dto.getIdPerfil()));
        return entity;
    }

    private UsuariosDTO convertToDTO(Usuarios entity) {
        UsuariosDTO dto = new UsuariosDTO();
        dto.setIdUsuario(entity.getIdUsuario());
        dto.setNombre(entity.getNombre());
        dto.setUsuario(entity.getUsuario());
        dto.setContrasena(entity.getContrasena());
        dto.setCorreo(entity.getCorreo());
        dto.setEstado(entity.getEstado());
        dto.setIdPerfil(entity.getIdPerfil() != null ? entity.getIdPerfil().getIdPerfil() : null);
        return dto;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuarios entity = usuariosRepository.findByUsuario(dto.getUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));

        if (!entity.getContrasena().equals(dto.getContrasena())) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }
        if (!entity.getEstado()) {
            throw new RuntimeException("El usuario está inactivo");
        }

        String token = jwtService.generarToken(
                entity.getUsuario(), entity.getIdUsuario(), entity.getIdPerfil().getIdPerfil());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setIdUsuario(entity.getIdUsuario());
        response.setNombre(entity.getNombre());
        response.setUsuario(entity.getUsuario());
        response.setCorreo(entity.getCorreo());
        response.setIdPerfil(entity.getIdPerfil().getIdPerfil());
        response.setNombrePerfil(entity.getIdPerfil().getNombrePerfil());
        response.setToken(token);
        return response;
    }
}