package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.dto.DetalleCitaDTO;
import com.proyecto.veterinaria.entity.DetalleCita;
import com.proyecto.veterinaria.entity.Cita;
import com.proyecto.veterinaria.entity.Medicamentos;
import com.proyecto.veterinaria.entity.Tratamiento;
import com.proyecto.veterinaria.repository.DetalleCitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetalleCitaService {

    private final DetalleCitaRepository detalleCitaRepository;

    public DetalleCitaService(DetalleCitaRepository detalleCitaRepository) {
        this.detalleCitaRepository = detalleCitaRepository;
    }

    public List<DetalleCitaDTO> findAll() {
        return detalleCitaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DetalleCitaDTO> findActivos() {
        return detalleCitaRepository.findByEstadoTrueOrderByIdDetalleCitaDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    //FILTRO DE CITA//
    public List<DetalleCitaDTO> findByCita(Integer idCita) {
        return detalleCitaRepository.findByIdCita_IdCita(idCita)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    //FILTRO DE MEDIDCAMENTOS//
    public List<DetalleCitaDTO> findByMedicamento(Integer idMedicamento) {
        return detalleCitaRepository.findByIdMedicamento_IdMedicamento(idMedicamento)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    //FILTRO DE TRATAMIENTOS//
    public List<DetalleCitaDTO> findByTratamiento(Integer idTratamiento) {
        return detalleCitaRepository.findByIdTratamiento_IdTratamiento(idTratamiento)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    //BUSCAR LOS DETALLES DE LA CITA//
    public DetalleCitaDTO findById(Integer id) {
        DetalleCita entity = detalleCitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleCita no encontrado"));
        return convertToDTO(entity);
    }

    public DetalleCitaDTO crear(DetalleCitaDTO dto) {
        DetalleCita entity = convertToEntity(dto);
        return convertToDTO(detalleCitaRepository.save(entity));
    }

    public DetalleCitaDTO actualizar(Integer id, DetalleCitaDTO dto) {
        DetalleCita entity = detalleCitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleCita no encontrado"));

        entity.setPrecioAplicado(dto.getPrecioAplicado());
        entity.setEstado(dto.getEstado());
        if (dto.getIdCita() != null)
            entity.setIdCita(new Cita(dto.getIdCita()));
        if (dto.getIdMedicamento() != null)
            entity.setIdMedicamento(new Medicamentos(dto.getIdMedicamento()));
        if (dto.getIdTratamiento() != null)
            entity.setIdTratamiento(new Tratamiento(dto.getIdTratamiento()));

        return convertToDTO(detalleCitaRepository.save(entity));
    }

    public void eliminar(Integer id) {
        detalleCitaRepository.deleteById(id);
    }

    public DetalleCitaDTO anular(Integer id) {
        DetalleCita entity = detalleCitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleCita no encontrado"));
        entity.setEstado(false);
        return convertToDTO(detalleCitaRepository.save(entity));
    }

    private DetalleCita convertToEntity(DetalleCitaDTO dto) {
        DetalleCita entity = new DetalleCita();
        entity.setPrecioAplicado(dto.getPrecioAplicado());
        entity.setEstado(dto.getEstado());
        if (dto.getIdCita() != null)
            entity.setIdCita(new Cita(dto.getIdCita()));
        if (dto.getIdMedicamento() != null)
            entity.setIdMedicamento(new Medicamentos(dto.getIdMedicamento()));
        if (dto.getIdTratamiento() != null)
            entity.setIdTratamiento(new Tratamiento(dto.getIdTratamiento()));
        return entity;
    }

    private DetalleCitaDTO convertToDTO(DetalleCita entity) {
        DetalleCitaDTO dto = new DetalleCitaDTO();
        dto.setIdDetalleCita(entity.getIdDetalleCita());
        dto.setPrecioAplicado(entity.getPrecioAplicado());
        dto.setEstado(entity.getEstado());
        dto.setIdCita(entity.getIdCita() != null ? entity.getIdCita().getIdCita() : null);
        dto.setIdMedicamento(entity.getIdMedicamento() != null ? entity.getIdMedicamento().getIdMedicamento() : null);
        dto.setIdTratamiento(entity.getIdTratamiento() != null ? entity.getIdTratamiento().getIdTratamiento() : null);
        return dto;
    }
}
