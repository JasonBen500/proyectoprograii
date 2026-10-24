package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.exception.RecursoNoEncontradoException;
import com.proyecto.veterinaria.dto.DetalleCitaDTO;
import com.proyecto.veterinaria.entity.DetalleCita;
import com.proyecto.veterinaria.entity.Cita;
import com.proyecto.veterinaria.entity.Medicamentos;
import com.proyecto.veterinaria.entity.Tratamiento;
import com.proyecto.veterinaria.repository.CitaRepository;
import com.proyecto.veterinaria.repository.DetalleCitaRepository;
import com.proyecto.veterinaria.repository.MedicamentosRepository;
import com.proyecto.veterinaria.repository.TratamientoRepository;
import com.proyecto.veterinaria.exception.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DetalleCitaService {

    private final DetalleCitaRepository detalleCitaRepository;
    private final MedicamentosService medicamentosService;
    private final CitaRepository citaRepository;
    private final MedicamentosRepository medicamentosRepository;
    private final TratamientoRepository tratamientoRepository;

    // SOLUCIÓN: Se agregó la declaración de HistorialService
    private final HistorialService historialService;

    public DetalleCitaService(DetalleCitaRepository detalleCitaRepository,
            MedicamentosService medicamentosService,
            CitaRepository citaRepository,
            MedicamentosRepository medicamentosRepository,
            TratamientoRepository tratamientoRepository,
            HistorialService historialService) {
        this.detalleCitaRepository = detalleCitaRepository;
        this.medicamentosService = medicamentosService;
        this.citaRepository = citaRepository;
        this.medicamentosRepository = medicamentosRepository;
        this.tratamientoRepository = tratamientoRepository;
        // SOLUCIÓN: Se asignó el servicio inyectado al atributo
        this.historialService = historialService;
    }

    // Un detalle lleva un medicamento o un tratamiento, nunca los dos ni ninguno
    private void validarItem(DetalleCitaDTO dto) {
        boolean conMedicamento = dto.getIdMedicamento() != null;
        boolean conTratamiento = dto.getIdTratamiento() != null;
        if (conMedicamento == conTratamiento) {
            throw new ReglaNegocioException(
                    "El detalle debe tener un medicamento o un tratamiento (solo uno de los dos).");
        }
    }

    private void validarCita(Integer idCita, boolean exigirActiva) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada"));
        if (exigirActiva && !Boolean.TRUE.equals(cita.getEstado())) {
            throw new ReglaNegocioException("No se pueden agregar detalles activos a una cita anulada.");
        }
    }

    // El precio siempre sale del catálogo (nunca del cliente)
    private BigDecimal precioDeCatalogo(DetalleCitaDTO dto) {
        BigDecimal precio;
        if (dto.getIdMedicamento() != null) {
            Medicamentos m = medicamentosRepository.findById(dto.getIdMedicamento())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Medicamento no encontrado"));
            if (!Boolean.TRUE.equals(m.getEstado())) {
                throw new ReglaNegocioException("El medicamento " + m.getNombre() + " está anulado.");
            }
            precio = m.getPrecio();
        } else {
            Tratamiento t = tratamientoRepository.findById(dto.getIdTratamiento())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Tratamiento no encontrado"));
            if (!Boolean.TRUE.equals(t.getEstado())) {
                throw new ReglaNegocioException("El tratamiento " + t.getNombre() + " está anulado.");
            }
            precio = t.getPrecio();
        }
        if (precio == null) {
            throw new ReglaNegocioException("El elemento seleccionado no tiene precio en el catálogo.");
        }
        return precio;
    }

    // El total de la cita siempre es la suma de sus detalles activos
    private void recalcularTotal(Integer idCita) {
        if (idCita == null) {
            return;
        }
        citaRepository.findById(idCita).ifPresent(cita -> {
            BigDecimal total = detalleCitaRepository.findByIdCita_IdCita(idCita).stream()
                    .filter(d -> Boolean.TRUE.equals(d.getEstado()) && d.getPrecioAplicado() != null)
                    .map(d -> d.getPrecioAplicado().multiply(BigDecimal.valueOf(cantidadDe(d))))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cita.setTotal(total);
            citaRepository.save(cita);
        });
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

    // FILTRO DE MEDICAMENTOS//
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
                .orElseThrow(() -> new RecursoNoEncontradoException("DetalleCita no encontrado"));
        return convertToDTO(entity);
    }

    @Transactional
    public DetalleCitaDTO crear(DetalleCitaDTO dto) {
        validarItem(dto);
        validarCita(dto.getIdCita(), true);
        DetalleCita entity = convertToEntity(dto);
        entity.setPrecioAplicado(precioDeCatalogo(dto));
        if (entity.getEstado() == null) {
            entity.setEstado(true);
        }
        DetalleCita guardado = detalleCitaRepository.save(entity);
        descontarInventario(guardado);
        recalcularTotal(dto.getIdCita());
        historialService.registrar("CREAR", "Detalle de cita",
                "Se agregó " + nombreItem(guardado) + " (x" + guardado.getCantidad()
                        + ") a la cita" + descripcionCita(dto.getIdCita()));
        return convertToDTO(guardado);
    }

    @Transactional
    public DetalleCitaDTO actualizar(Integer id, DetalleCitaDTO dto) {
        DetalleCita entity = detalleCitaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("DetalleCita no encontrado"));

        validarItem(dto);
        boolean activo = dto.getEstado() == null || Boolean.TRUE.equals(dto.getEstado());
        validarCita(dto.getIdCita(), activo);

        Integer citaAnterior = entity.getIdCita() != null ? entity.getIdCita().getIdCita() : null;
        Integer medicamentoAnterior = entity.getIdMedicamento() != null
                ? entity.getIdMedicamento().getIdMedicamento()
                : null;
        Integer tratamientoAnterior = entity.getIdTratamiento() != null
                ? entity.getIdTratamiento().getIdTratamiento()
                : null;
        boolean mismoItem = Objects.equals(medicamentoAnterior, dto.getIdMedicamento())
                && Objects.equals(tratamientoAnterior, dto.getIdTratamiento());

        // Mismo item: se conserva el precio ya aplicado; item distinto: precio actual
        // del catálogo
        BigDecimal precio = mismoItem && entity.getPrecioAplicado() != null
                ? entity.getPrecioAplicado()
                : precioDeCatalogo(dto);

        // Devuelve al inventario lo que consumía antes de editar
        reponerInventario(entity);

        entity.setPrecioAplicado(precio);
        entity.setEstado(activo);
        entity.setCantidad(dto.getCantidad() == null ? 1 : dto.getCantidad());
        entity.setIdCita(new Cita(dto.getIdCita()));
        // Se asigna siempre (también null) para que cambiar de medicamento a
        // tratamiento limpie el otro
        entity.setIdMedicamento(dto.getIdMedicamento() != null ? new Medicamentos(dto.getIdMedicamento()) : null);
        entity.setIdTratamiento(dto.getIdTratamiento() != null ? new Tratamiento(dto.getIdTratamiento()) : null);

        DetalleCita guardado = detalleCitaRepository.save(entity);

        // Descuenta lo que consume ahora
        descontarInventario(guardado);
        recalcularTotal(citaAnterior);
        recalcularTotal(guardado.getIdCita() != null ? guardado.getIdCita().getIdCita() : null);
        historialService.registrar("MODIFICAR", "Detalle de cita",
                "Se modificó " + nombreItem(guardado) + " de la cita" + descripcionCita(dto.getIdCita()));
        return convertToDTO(guardado);
    }

    @Transactional
    public DetalleCitaDTO anular(Integer id) {
        DetalleCita entity = detalleCitaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("DetalleCita no encontrado"));
        reponerInventario(entity); // antes de marcarlo como anulado
        entity.setEstado(false);
        DetalleCita guardado = detalleCitaRepository.save(entity);
        recalcularTotal(guardado.getIdCita() != null ? guardado.getIdCita().getIdCita() : null);
        historialService.registrar("ANULAR", "Detalle de cita",
                "Se anuló " + nombreItem(guardado) + " de la cita"
                        + descripcionCita(guardado.getIdCita() != null ? guardado.getIdCita().getIdCita() : null));
        return convertToDTO(guardado);
    }

    // " de Firulais (20/10/2026 10:30)": la cita descrita por su paciente, sin mostrar ids
    private String descripcionCita(Integer idCita) {
        return Objects.toString(historialService.describirCita(idCita), "");
    }

    private String nombreItem(DetalleCita d) {
        if (d.getIdMedicamento() != null) {
            return medicamentosRepository.findById(d.getIdMedicamento().getIdMedicamento())
                    .map(Medicamentos::getNombre).orElse("medicamento");
        }
        if (d.getIdTratamiento() != null) {
            return tratamientoRepository.findById(d.getIdTratamiento().getIdTratamiento())
                    .map(Tratamiento::getNombre).orElse("tratamiento");
        }
        return "detalle";
    }

    // Anula todos los detalles activos de la cita y devuelve su stock
    @Transactional
    public void anularPorCita(Integer idCita) {
        List<DetalleCita> detalles = detalleCitaRepository.findByIdCita_IdCita(idCita);
        for (DetalleCita d : detalles) {
            if (Boolean.TRUE.equals(d.getEstado())) {
                reponerInventario(d);
                d.setEstado(false);
            }
        }
        detalleCitaRepository.saveAll(detalles);
        recalcularTotal(idCita);
    }

    @Transactional
    public void eliminarPorCita(Integer idCita) {
        List<DetalleCita> detalles = detalleCitaRepository.findByIdCita_IdCita(idCita);
        detalles.forEach(this::reponerInventario);
        detalleCitaRepository.deleteAll(detalles);
    }

    @Transactional
    public void eliminar(Integer id) {
        Integer idCita = detalleCitaRepository.findById(id).map(d -> {
            reponerInventario(d);
            return d.getIdCita() != null ? d.getIdCita().getIdCita() : null;
        }).orElse(null);
        detalleCitaRepository.deleteById(id);
        recalcularTotal(idCita);
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