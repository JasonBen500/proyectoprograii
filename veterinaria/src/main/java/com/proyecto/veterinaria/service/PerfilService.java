package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.dto.PerfilDTO;
import com.proyecto.veterinaria.entity.Perfil;
import com.proyecto.veterinaria.repository.PerfilRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerfilService {

    private final PerfilRepository perfilRepository;

    public PerfilService(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    public List<PerfilDTO> findAll() {
        return perfilRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PerfilDTO findById(Integer id) {
        Perfil entity = perfilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
        return convertToDTO(entity);
    }

    public PerfilDTO crear(PerfilDTO dto) {
        Perfil entity = convertToEntity(dto);
        return convertToDTO(perfilRepository.save(entity));
    }

    public PerfilDTO actualizar(Integer id, PerfilDTO dto) {
        Perfil entity = perfilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));

        entity.setNombrePerfil(dto.getNombrePerfil());
        entity.setEstado(dto.getEstado());

        return convertToDTO(perfilRepository.save(entity));
    }

    public void eliminar(Integer id) {
        perfilRepository.deleteById(id);
    }

    public PerfilDTO anular(Integer id) {
        Perfil entity = perfilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
        entity.setEstado(false);
        return convertToDTO(perfilRepository.save(entity));
    }

    public List<PerfilDTO> findActivos() {
        return perfilRepository.findByEstadoTrueOrderByIdPerfilDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PerfilDTO findByNombre(String nombrePerfil) {
        Perfil entity = perfilRepository.findByNombrePerfilIgnoreCase(nombrePerfil)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
        return convertToDTO(entity);
    }

    public boolean existeNombre(String nombrePerfil) {
        return perfilRepository.existsByNombrePerfilIgnoreCase(nombrePerfil);
    }

    private Perfil convertToEntity(PerfilDTO dto) {
        Perfil entity = new Perfil();
        entity.setNombrePerfil(dto.getNombrePerfil());
        entity.setEstado(dto.getEstado());
        return entity;
    }

    private PerfilDTO convertToDTO(Perfil entity) {
        PerfilDTO dto = new PerfilDTO();
        dto.setIdPerfil(entity.getIdPerfil());
        dto.setNombrePerfil(entity.getNombrePerfil());
        dto.setEstado(entity.getEstado());
        return dto;
    }
}