package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.exception.RecursoNoEncontradoException;

import com.proyecto.veterinaria.dto.TratamientoDTO;
import com.proyecto.veterinaria.entity.Tratamiento;
import com.proyecto.veterinaria.repository.TratamientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TratamientoService {

    private final TratamientoRepository tratamientoRepository;
    private final HistorialService historialService;

    public TratamientoService(TratamientoRepository tratamientoRepository, HistorialService historialService) {
        this.tratamientoRepository = tratamientoRepository;
        this.historialService = historialService;
    }

    public List<TratamientoDTO> findAll() {
        return tratamientoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TratamientoDTO findById(Integer id) {
        Tratamiento entity = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tratamiento no encontrado"));
        return convertToDTO(entity);
    }

    public TratamientoDTO crear(TratamientoDTO dto) {
        Tratamiento entity = convertToEntity(dto);
        Tratamiento guardado = tratamientoRepository.save(entity);
        historialService.registrar("CREAR", "Tratamiento", "Se agregó el tratamiento " + guardado.getNombre());
        return convertToDTO(guardado);
    }

    public TratamientoDTO actualizar(Integer id, TratamientoDTO dto) {
        Tratamiento entity = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tratamiento no encontrado"));

        entity.setNombre(dto.getNombre());
        entity.setPrecio(dto.getPrecio());
        entity.setEstado(dto.getEstado());

        Tratamiento guardado = tratamientoRepository.save(entity);
        historialService.registrar("MODIFICAR", "Tratamiento", "Se modificó el tratamiento " + guardado.getNombre());
        return convertToDTO(guardado);
    }

    public TratamientoDTO anular(Integer id) {
        Tratamiento entity = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tratamiento no encontrado"));
        entity.setEstado(false);
        Tratamiento guardado = tratamientoRepository.save(entity);
        historialService.registrar("ANULAR", "Tratamiento", "Se anuló el tratamiento " + guardado.getNombre());
        return convertToDTO(guardado);
    }

    public List<TratamientoDTO> findActivos() {
        return tratamientoRepository.findByEstadoTrueOrderByIdTratamientoDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TratamientoDTO> buscarPorNombre(String nombre) {
        return tratamientoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean existeNombre(String nombre) {
        return tratamientoRepository.existsByNombreIgnoreCase(nombre);
    }

    private Tratamiento convertToEntity(TratamientoDTO dto) {
        Tratamiento entity = new Tratamiento();
        entity.setNombre(dto.getNombre());
        entity.setPrecio(dto.getPrecio());
        entity.setEstado(dto.getEstado());
        return entity;
    }

    private TratamientoDTO convertToDTO(Tratamiento entity) {
        TratamientoDTO dto = new TratamientoDTO();
        dto.setIdTratamiento(entity.getIdTratamiento());
        dto.setNombre(entity.getNombre());
        dto.setPrecio(entity.getPrecio());
        dto.setEstado(entity.getEstado());
        return dto;
    }
}