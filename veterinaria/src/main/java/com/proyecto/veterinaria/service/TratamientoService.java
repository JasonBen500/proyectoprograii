package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.dto.TratamientoDTO;
import com.proyecto.veterinaria.entity.Tratamiento;
import com.proyecto.veterinaria.repository.TratamientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TratamientoService {

    private final TratamientoRepository tratamientoRepository;

    public TratamientoService(TratamientoRepository tratamientoRepository) {
        this.tratamientoRepository = tratamientoRepository;
    }

    public List<TratamientoDTO> findAll() {
        return tratamientoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TratamientoDTO findById(Integer id) {
        Tratamiento entity = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));
        return convertToDTO(entity);
    }

    public TratamientoDTO crear(TratamientoDTO dto) {
        Tratamiento entity = convertToEntity(dto);
        return convertToDTO(tratamientoRepository.save(entity));
    }

    public TratamientoDTO actualizar(Integer id, TratamientoDTO dto) {
        Tratamiento entity = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));

        entity.setNombre(dto.getNombre());
        entity.setPrecio(dto.getPrecio());
        entity.setEstado(dto.getEstado());

        return convertToDTO(tratamientoRepository.save(entity));
    }

    public void eliminar(Integer id) {
        tratamientoRepository.deleteById(id);
    }

    public TratamientoDTO anular(Integer id) {
        Tratamiento entity = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));
        entity.setEstado(false);
        return convertToDTO(tratamientoRepository.save(entity));
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