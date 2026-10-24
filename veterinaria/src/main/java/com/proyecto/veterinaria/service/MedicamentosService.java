package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.exception.RecursoNoEncontradoException;

import com.proyecto.veterinaria.dto.MedicamentosDTO;
import com.proyecto.veterinaria.entity.Medicamentos;
import com.proyecto.veterinaria.repository.MedicamentosRepository;
import org.springframework.stereotype.Service;
import com.proyecto.veterinaria.exception.ReglaNegocioException;
import com.proyecto.veterinaria.repository.DetalleCitaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicamentosService {

    private final MedicamentosRepository medicamentosRepository;
    private final DetalleCitaRepository detalleCitaRepository;
    private final HistorialService historialService;

    public MedicamentosService(MedicamentosRepository medicamentosRepository,
            DetalleCitaRepository detalleCitaRepository,
            HistorialService historialService) {
        this.medicamentosRepository = medicamentosRepository;
        this.detalleCitaRepository = detalleCitaRepository;
        this.historialService = historialService;
    }

    public List<MedicamentosDTO> findAll() {
        return medicamentosRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MedicamentosDTO findById(Integer id) {
        Medicamentos entity = medicamentosRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento no encontrado"));
        return convertToDTO(entity);
    }

    public MedicamentosDTO crear(MedicamentosDTO dto) {
        Medicamentos entity = convertToEntity(dto);
        Medicamentos guardado = medicamentosRepository.save(entity);
        historialService.registrar("CREAR", "Medicamento", "Se agregó el medicamento " + guardado.getNombre());
        return convertToDTO(guardado);
    }

    public MedicamentosDTO actualizar(Integer id, MedicamentosDTO dto) {
        Medicamentos entity = medicamentosRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento no encontrado"));

        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecio(dto.getPrecio());
        entity.setStock(dto.getStock());
        entity.setEstado(dto.getEstado());

        Medicamentos guardado = medicamentosRepository.save(entity);
        historialService.registrar("MODIFICAR", "Medicamento", "Se modificó el medicamento " + guardado.getNombre());
        return convertToDTO(guardado);
    }

    public void eliminar(Integer id) {
        if (detalleCitaRepository.existsByIdMedicamento_IdMedicamento(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar: el medicamento ya se usó en citas. Use Anular.");
        }
        String nombre = medicamentosRepository.findById(id).map(Medicamentos::getNombre).orElse("#" + id);
        medicamentosRepository.deleteById(id);
        historialService.registrar("ELIMINAR", "Medicamento", "Se eliminó el medicamento " + nombre);
    }

    public MedicamentosDTO anular(Integer id) {
        Medicamentos entity = medicamentosRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento no encontrado"));
        entity.setEstado(false);
        Medicamentos guardado = medicamentosRepository.save(entity);
        historialService.registrar("ANULAR", "Medicamento", "Se anuló el medicamento " + guardado.getNombre());
        return convertToDTO(guardado);
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

    @Transactional
    public MedicamentosDTO ajustarStock(Integer id, Integer cambio) {
        int filas = medicamentosRepository.ajustarStockAtomico(id, cambio);

        Medicamentos entity = medicamentosRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento no encontrado"));

        if (filas == 0) {
            int actual = entity.getStock() == null ? 0 : entity.getStock();
            throw new ReglaNegocioException(
                    "Stock insuficiente de " + entity.getNombre() + ": hay " + actual + " disponibles.");
        }
        return convertToDTO(entity);
    }

    @Transactional
    public MedicamentosDTO ajustarStockManual(Integer id, Integer cambio) {
        MedicamentosDTO dto = ajustarStock(id, cambio);
        historialService.registrar("MODIFICAR", "Medicamento",
                "Se " + (cambio >= 0 ? "sumaron " : "restaron ") + Math.abs(cambio)
                        + " unidades al stock de " + dto.getNombre());
        return dto;
    }

    // Texto vacío = sin imagen
    private String normalizarImagen(String imagen) {
        return imagen == null || imagen.isBlank() ? null : imagen;
    }

    private Medicamentos convertToEntity(MedicamentosDTO dto) {
        Medicamentos entity = new Medicamentos();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecio(dto.getPrecio());
        entity.setStock(dto.getStock());
        entity.setEstado(dto.getEstado());
        entity.setImagen(normalizarImagen(dto.getImagen()));
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
        dto.setImagen(entity.getImagen());
        return dto;
    }
}
