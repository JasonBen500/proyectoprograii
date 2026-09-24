package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.dto.MedicamentosDTO;
import com.proyecto.veterinaria.entity.Medicamentos;
import com.proyecto.veterinaria.repository.MedicamentosRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicamentosService {

    private final MedicamentosRepository medicamentosRepository;

    public MedicamentosService(MedicamentosRepository medicamentosRepository) {
        this.medicamentosRepository = medicamentosRepository;
    }

    public List<MedicamentosDTO> findAll() {
        return medicamentosRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MedicamentosDTO findById(Integer id) {
        Medicamentos entity = medicamentosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));
        return convertToDTO(entity);
    }

    public MedicamentosDTO crear(MedicamentosDTO dto) {
        Medicamentos entity = convertToEntity(dto);
        return convertToDTO(medicamentosRepository.save(entity));
    }

    public MedicamentosDTO actualizar(Integer id, MedicamentosDTO dto) {
        Medicamentos entity = medicamentosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));

        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecio(dto.getPrecio());
        entity.setStock(dto.getStock());
        entity.setEstado(dto.getEstado());

        return convertToDTO(medicamentosRepository.save(entity));
    }

    public void eliminar(Integer id) {
        medicamentosRepository.deleteById(id);
    }

    public MedicamentosDTO anular(Integer id) {
        Medicamentos entity = medicamentosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));
        entity.setEstado(false);
        return convertToDTO(medicamentosRepository.save(entity));
    }

    public List<MedicamentosDTO> findActivos() {
        return medicamentosRepository.findByEstadoTrueOrderByIdMedicamentoDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MedicamentosDTO> buscarPorNombre(String nombre) {
        return medicamentosRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MedicamentosDTO> findStockBajo(Integer cantidad) {
        return medicamentosRepository.findByStockLessThan(cantidad)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean existeNombre(String nombre) {
        return medicamentosRepository.existsByNombreIgnoreCase(nombre);
    }

    private Medicamentos convertToEntity(MedicamentosDTO dto) {
        Medicamentos entity = new Medicamentos();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecio(dto.getPrecio());
        entity.setStock(dto.getStock());
        entity.setEstado(dto.getEstado());
        return entity;
    }

    private MedicamentosDTO convertToDTO(Medicamentos entity) {
        MedicamentosDTO dto = new MedicamentosDTO();
        dto.setIdMedicamento(entity.getIdMedicamento());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setPrecio(entity.getPrecio());
        dto.setStock(entity.getStock());
        dto.setEstado(entity.getEstado());
        return dto;
    }
}
