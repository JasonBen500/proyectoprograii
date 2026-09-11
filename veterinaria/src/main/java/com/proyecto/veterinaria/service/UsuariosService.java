package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.dto.UsuariosDTO;
import com.proyecto.veterinaria.entity.Usuarios;
import com.proyecto.veterinaria.entity.Perfil;
import com.proyecto.veterinaria.repository.UsuariosRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;

    public UsuariosService(UsuariosRepository usuariosRepository) {
        this.usuariosRepository = usuariosRepository;
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
        if (dto.getIdPerfil() != null) entity.setIdPerfil(new Perfil(dto.getIdPerfil()));

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
        if (dto.getIdPerfil() != null) entity.setIdPerfil(new Perfil(dto.getIdPerfil()));
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
}