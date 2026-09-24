package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.dto.ConsultaDTO;
import com.proyecto.veterinaria.entity.Consulta;
import com.proyecto.veterinaria.entity.Cita;
import com.proyecto.veterinaria.repository.ConsultaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;

    public ConsultaService(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }


    //ENCONTRAR A TODOS
    public List<ConsultaDTO> findAll() {
        return consultaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //ENCONTRAR A LOS ACTIVOS
    public List<ConsultaDTO> findActivas() {
        return consultaRepository.findByEstadoTrueOrderByIdConsultaDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //FILTRO DE CITAS
    public List<ConsultaDTO> findByCita(Integer idCita) {
        return consultaRepository.findByIdCita_IdCita(idCita)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    //FFILTRO DE DIAGNOSTICO
    public List<ConsultaDTO> buscarPorDiagnostico(String diagnostico) {
        return consultaRepository.findByDiagnosticoContainingIgnoreCase(diagnostico)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //FILTRO DE ID
    public ConsultaDTO findById(Integer id) {
        Consulta entity = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        return convertToDTO(entity);
    }

    public ConsultaDTO crear(ConsultaDTO dto) {
        Consulta entity = convertToEntity(dto);
        return convertToDTO(consultaRepository.save(entity));
    }

    public ConsultaDTO actualizar(Integer id, ConsultaDTO dto) {
        Consulta entity = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));

        entity.setDiagnostico(dto.getDiagnostico());
        entity.setSintomas(dto.getSintomas());
        entity.setEstado(dto.getEstado());
        if (dto.getIdCita() != null)
            entity.setIdCita(new Cita(dto.getIdCita()));

        return convertToDTO(consultaRepository.save(entity));
    }

    public void eliminar(Integer id) {
        consultaRepository.deleteById(id);
    }

    public ConsultaDTO anular(Integer id) {
        Consulta entity = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        entity.setEstado(false);
        return convertToDTO(consultaRepository.save(entity));
    }

    private Consulta convertToEntity(ConsultaDTO dto) {
        Consulta entity = new Consulta();
        entity.setDiagnostico(dto.getDiagnostico());
        entity.setSintomas(dto.getSintomas());
        entity.setEstado(dto.getEstado());
        if (dto.getIdCita() != null)
            entity.setIdCita(new Cita(dto.getIdCita()));
        return entity;
    }

    private ConsultaDTO convertToDTO(Consulta entity) {
        ConsultaDTO dto = new ConsultaDTO();
        dto.setIdConsulta(entity.getIdConsulta());
        dto.setDiagnostico(entity.getDiagnostico());
        dto.setSintomas(entity.getSintomas());
        dto.setEstado(entity.getEstado());
        dto.setIdCita(entity.getIdCita() != null ? entity.getIdCita().getIdCita() : null);
        return dto;
    }
}