package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.exception.RecursoNoEncontradoException;

import com.proyecto.veterinaria.dto.PerfilDTO;
import com.proyecto.veterinaria.entity.Perfil;
import com.proyecto.veterinaria.repository.PerfilRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final HistorialService historialService;

    public PerfilService(PerfilRepository perfilRepository, HistorialService historialService) {
        this.perfilRepository = perfilRepository;
        this.historialService = historialService;
    }

    public List<PerfilDTO> findAll() {
        return perfilRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PerfilDTO findById(Integer id) {
        Perfil entity = perfilRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado"));
        return convertToDTO(entity);
    }

    public PerfilDTO crear(PerfilDTO dto) {
        Perfil entity = convertToEntity(dto);
        Perfil guardado = perfilRepository.save(entity);
        historialService.registrar("CREAR", "Perfil", "Se agregó el perfil " + guardado.getNombrePerfil());
        return convertToDTO(guardado);
    }

    public PerfilDTO actualizar(Integer id, PerfilDTO dto) {
        Perfil entity = perfilRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado"));

        entity.setNombrePerfil(dto.getNombrePerfil());
        entity.setEstado(dto.getEstado());

        Perfil guardado = perfilRepository.save(entity);
        historialService.registrar("MODIFICAR", "Perfil", "Se modificó el perfil " + guardado.getNombrePerfil());
        return convertToDTO(guardado);
    }

    public PerfilDTO anular(Integer id) {
        Perfil entity = perfilRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado"));
        entity.setEstado(false);
        Perfil guardado = perfilRepository.save(entity);
        historialService.registrar("ANULAR", "Perfil", "Se anuló el perfil " + guardado.getNombrePerfil());
        return convertToDTO(guardado);
    }

    public List<PerfilDTO> findActivos() {
        return perfilRepository.findByEstadoTrueOrderByIdPerfilDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PerfilDTO findByNombre(String nombrePerfil) {
        Perfil entity = perfilRepository.findByNombrePerfilIgnoreCase(nombrePerfil)
                .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado"));
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