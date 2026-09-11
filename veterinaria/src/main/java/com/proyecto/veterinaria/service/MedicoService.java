package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.dto.MedicoDTO;
import com.proyecto.veterinaria.entity.Medico;
import com.proyecto.veterinaria.entity.Usuarios;
import com.proyecto.veterinaria.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public List<MedicoDTO> findAll() {
        return medicoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MedicoDTO findById(Integer id) {
        Medico entity = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medico no encontrado"));
        return convertToDTO(entity);
    }

    public MedicoDTO agregar(MedicoDTO dto) {
        Medico entity = convertToEntity(dto);
        return convertToDTO(medicoRepository.save(entity));
    }

    public MedicoDTO modificar(Integer id, MedicoDTO dto) {
        Medico entity = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medico no encontrado"));

        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setCedula(dto.getCedula());
        entity.setEspecialidad(dto.getEspecialidad());
        entity.setEstado(dto.getEstado());
        if (dto.getIdUsuario() != null) entity.setIdUsuario(new Usuarios(dto.getIdUsuario()));

        return convertToDTO(medicoRepository.save(entity));
    }

    public void eliminar(Integer id) {
        medicoRepository.deleteById(id);
    }

    public MedicoDTO anular(Integer id) {
        Medico entity = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medico no encontrado"));
        entity.setEstado(false);
        return convertToDTO(medicoRepository.save(entity));
    }

    private Medico convertToEntity(MedicoDTO dto) {
        Medico entity = new Medico();
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setCedula(dto.getCedula());
        entity.setEspecialidad(dto.getEspecialidad());
        entity.setEstado(dto.getEstado());
        if (dto.getIdUsuario() != null) entity.setIdUsuario(new Usuarios(dto.getIdUsuario()));
        return entity;
    }

    private MedicoDTO convertToDTO(Medico entity) {
        MedicoDTO dto = new MedicoDTO();
        dto.setIdMedico(entity.getIdMedico());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setCedula(entity.getCedula());
        dto.setEspecialidad(entity.getEspecialidad());
        dto.setEstado(entity.getEstado());
        dto.setIdUsuario(entity.getIdUsuario() != null ? entity.getIdUsuario().getIdUsuario() : null);
        return dto;
    }
}