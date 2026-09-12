package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.dto.CitaDTO;
import com.proyecto.veterinaria.entity.Cita;
import com.proyecto.veterinaria.entity.Mascota;
import com.proyecto.veterinaria.entity.Medico;
import com.proyecto.veterinaria.entity.Usuarios;
import com.proyecto.veterinaria.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaService {

    private final CitaRepository citaRepository;

    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public List<CitaDTO> findAll() {
        return citaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CitaDTO> filtroActivas() {
    return citaRepository.findByEstadoTrueOrderByIdCitaDesc()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

    public List<CitaDTO> filtroMascota(Integer idMascota) {
    return citaRepository.findByIdMascota_IdMascota(idMascota)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

    public List<CitaDTO> filtroMedico(Integer idMedico) {
    return citaRepository.findByIdMedico_IdMedico(idMedico)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

    public CitaDTO findById(Integer id) {
        Cita entity = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        return convertToDTO(entity);
    }

    public CitaDTO crear(CitaDTO dto) {
        Cita entity = convertToEntity(dto);
        return convertToDTO(citaRepository.save(entity));
    }

    public CitaDTO modificar(Integer id, CitaDTO dto) {
        Cita entity = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        entity.setFecha(dto.getFecha());
        entity.setMotivo(dto.getMotivo());
        entity.setTotal(dto.getTotal());
        entity.setEstado(dto.getEstado());
        if (dto.getIdMascota() != null) entity.setIdMascota(new Mascota(dto.getIdMascota()));
        if (dto.getIdMedico() != null) entity.setIdMedico(new Medico(dto.getIdMedico()));
        if (dto.getIdUsuario() != null) entity.setIdUsuario(new Usuarios(dto.getIdUsuario()));

        return convertToDTO(citaRepository.save(entity));
    }

    public void eliminar(Integer id) {
        citaRepository.deleteById(id);
    }

    public CitaDTO anular(Integer id) {
        Cita entity = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        entity.setEstado(false);
        return convertToDTO(citaRepository.save(entity));
    }

    private Cita convertToEntity(CitaDTO dto) {
        Cita entity = new Cita();
        entity.setFecha(dto.getFecha());
        entity.setMotivo(dto.getMotivo());
        entity.setTotal(dto.getTotal());
        entity.setEstado(dto.getEstado());
        if (dto.getIdMascota() != null) entity.setIdMascota(new Mascota(dto.getIdMascota()));
        if (dto.getIdMedico() != null) entity.setIdMedico(new Medico(dto.getIdMedico()));
        if (dto.getIdUsuario() != null) entity.setIdUsuario(new Usuarios(dto.getIdUsuario()));
        return entity;
    }

    private CitaDTO convertToDTO(Cita entity) {
        CitaDTO dto = new CitaDTO();
        dto.setIdCita(entity.getIdCita());
        dto.setFecha(entity.getFecha());
        dto.setMotivo(entity.getMotivo());
        dto.setTotal(entity.getTotal());
        dto.setEstado(entity.getEstado());
        dto.setIdMascota(entity.getIdMascota() != null ? entity.getIdMascota().getIdMascota() : null);
        dto.setIdMedico(entity.getIdMedico() != null ? entity.getIdMedico().getIdMedico() : null);
        dto.setIdUsuario(entity.getIdUsuario() != null ? entity.getIdUsuario().getIdUsuario() : null);
        return dto;
    }
}