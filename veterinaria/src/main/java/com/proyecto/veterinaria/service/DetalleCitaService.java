package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.dto.DetalleCitaDTO;
import com.proyecto.veterinaria.entity.DetalleCita;
import com.proyecto.veterinaria.entity.Cita;
import com.proyecto.veterinaria.entity.Medicamentos;
import com.proyecto.veterinaria.entity.Tratamiento;
import com.proyecto.veterinaria.repository.DetalleCitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetalleCitaService {

    private final DetalleCitaRepository detalleCitaRepository;
    private final MedicamentosService medicamentosService;

    public DetalleCitaService(DetalleCitaRepository detalleCitaRepository,
            MedicamentosService medicamentosService) {
        this.detalleCitaRepository = detalleCitaRepository;
        this.medicamentosService = medicamentosService;
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

    // FILTRO DE CITA//
    public List<DetalleCitaDTO> findByCita(Integer idCita) {
        return detalleCitaRepository.findByIdCita_IdCita(idCita)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // FILTRO DE MEDIDCAMENTOS//
    public List<DetalleCitaDTO> findByMedicamento(Integer idMedicamento) {
        return detalleCitaRepository.findByIdMedicamento_IdMedicamento(idMedicamento)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // FILTRO DE TRATAMIENTOS//
    public List<DetalleCitaDTO> findByTratamiento(Integer idTratamiento) {
        return detalleCitaRepository.findByIdTratamiento_IdTratamiento(idTratamiento)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // BUSCAR LOS DETALLES DE LA CITA//
    public DetalleCitaDTO findById(Integer id) {
        DetalleCita entity = detalleCitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleCita no encontrado"));
        return convertToDTO(entity);
    }

    public DetalleCitaDTO crear(DetalleCitaDTO dto) {
        DetalleCita entity = convertToEntity(dto);
        return convertToDTO(detalleCitaRepository.save(entity));
    }

    @Transactional
    public DetalleCitaDTO actualizar(Integer id, DetalleCitaDTO dto) {
        DetalleCita entity = detalleCitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleCita no encontrado"));

        // Devuelve al inventario lo que consumía antes de editar
        reponerInventario(entity);

        entity.setPrecioAplicado(dto.getPrecioAplicado());
        entity.setEstado(dto.getEstado());
        entity.setCantidad(dto.getCantidad() == null ? 1 : dto.getCantidad());
        if (dto.getIdCita() != null)
            entity.setIdCita(new Cita(dto.getIdCita()));
        if (dto.getIdMedicamento() != null)
            entity.setIdMedicamento(new Medicamentos(dto.getIdMedicamento()));
        if (dto.getIdTratamiento() != null)
            entity.setIdTratamiento(new Tratamiento(dto.getIdTratamiento()));

        DetalleCita guardado = detalleCitaRepository.save(entity);

        // Descuenta lo que consume ahora
        descontarInventario(guardado);
        return convertToDTO(guardado);
    }

    @Transactional
    public DetalleCitaDTO anular(Integer id) {
        DetalleCita entity = detalleCitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleCita no encontrado"));
        reponerInventario(entity); // antes de marcarlo como anulado
        entity.setEstado(false);
        return convertToDTO(detalleCitaRepository.save(entity));
    }

    @Transactional
    public void eliminarPorCita(Integer idCita) {
        List<DetalleCita> detalles = detalleCitaRepository.findByIdCita_IdCita(idCita);
        detalles.forEach(this::reponerInventario);
        detalleCitaRepository.deleteAll(detalles);
    }

    @Transactional
    public void eliminar(Integer id) {
        detalleCitaRepository.findById(id).ifPresent(this::reponerInventario);
        detalleCitaRepository.deleteById(id);
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
        entity.setCantidad(dto.getCantidad() == null ? 1 : dto.getCantidad());
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
        dto.setDescripcionCita(NombresUtil.cita(entity.getIdCita()));
        dto.setNombreMedicamento(NombresUtil.medicamento(entity.getIdMedicamento()));
        dto.setNombreTratamiento(NombresUtil.tratamiento(entity.getIdTratamiento()));
        dto.setCantidad(entity.getCantidad());
        return dto;
    }

    // Un detalle consume inventario solo si está activo y tiene medicamento
    private boolean consumeInventario(DetalleCita detalle) {
        return Boolean.TRUE.equals(detalle.getEstado())
                && detalle.getIdMedicamento() != null;
    }

    private int cantidadDe(DetalleCita detalle) {
        return detalle.getCantidad() == null ? 1 : detalle.getCantidad();
    }

    private void descontarInventario(DetalleCita detalle) {
        if (consumeInventario(detalle)) {
            medicamentosService.ajustarStock(
                    detalle.getIdMedicamento().getIdMedicamento(), -cantidadDe(detalle));
        }
    }

    private void reponerInventario(DetalleCita detalle) {
        if (consumeInventario(detalle)) {
            medicamentosService.ajustarStock(
                    detalle.getIdMedicamento().getIdMedicamento(), cantidadDe(detalle));
        }
    }
}
