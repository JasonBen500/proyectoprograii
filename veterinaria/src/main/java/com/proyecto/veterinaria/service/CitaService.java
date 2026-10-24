package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.exception.RecursoNoEncontradoException;
import com.proyecto.veterinaria.dto.CitaDTO;
import com.proyecto.veterinaria.entity.Cita;
import com.proyecto.veterinaria.entity.Mascota;
import com.proyecto.veterinaria.entity.Medico;
import com.proyecto.veterinaria.entity.Usuarios;
import com.proyecto.veterinaria.repository.CitaRepository;
import org.springframework.stereotype.Service;
import com.proyecto.veterinaria.exception.ReglaNegocioException;
import com.proyecto.veterinaria.repository.ConsultaRepository;
import com.proyecto.veterinaria.repository.DetalleCitaRepository;
import com.proyecto.veterinaria.repository.MedicoRepository;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto.veterinaria.dto.HojaCitaDTO;
import com.proyecto.veterinaria.dto.HojaLineaDTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final ConsultaRepository consultaRepository;
    private final DetalleCitaService detalleCitaService;
    private final DetalleCitaRepository detalleCitaRepository;
    private final MedicoRepository medicoRepository;

    // SOLUCIÓN: Se agregó la declaración de HistorialService que faltaba
    private final HistorialService historialService;

    public CitaService(CitaRepository citaRepository,
            ConsultaRepository consultaRepository,
            DetalleCitaService detalleCitaService,
            DetalleCitaRepository detalleCitaRepository,
            MedicoRepository medicoRepository,
            HistorialService historialService) {
        this.citaRepository = citaRepository;
        this.historialService = historialService;
        this.consultaRepository = consultaRepository;
        this.detalleCitaService = detalleCitaService;
        this.detalleCitaRepository = detalleCitaRepository;
        this.medicoRepository = medicoRepository;
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
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada"));
        return convertToDTO(entity);
    }

    private static final long DURACION_CITA_MS = 30L * 60 * 1000;
    private static final long TOLERANCIA_PASADO_MS = 5L * 60 * 1000;

    private void validarAgenda(CitaDTO dto, Integer idCitaActual, boolean validarPasado) {
        if (dto.getFecha() == null) {
            return;
        }
        long ahora = System.currentTimeMillis();
        if (validarPasado && dto.getFecha().getTime() < ahora - TOLERANCIA_PASADO_MS) {
            throw new ReglaNegocioException("No se puede agendar una cita en una fecha pasada.");
        }
        if (dto.getIdMedico() == null
                || !Boolean.TRUE.equals(dto.getEstado() == null ? Boolean.TRUE : dto.getEstado())) {
            return;
        }
        Date inicio = new Date(dto.getFecha().getTime() - DURACION_CITA_MS + 1);
        Date fin = new Date(dto.getFecha().getTime() + DURACION_CITA_MS - 1);
        boolean choca = citaRepository
                .findByIdMedico_IdMedicoAndEstadoTrueAndFechaBetween(dto.getIdMedico(), inicio, fin)
                .stream()
                .anyMatch(c -> idCitaActual == null || !idCitaActual.equals(c.getIdCita()));
        if (choca) {
            throw new ReglaNegocioException(
                    "El médico ya tiene una cita en ese horario (cada cita ocupa 30 minutos).");
        }
    }

    // Bloquea al médico antes de revisar su agenda: así dos peticiones simultáneas
    // no pueden agendar el mismo horario. Debe llamarse al inicio de la
    // transacción.
    private void bloquearMedico(Integer idMedico) {
        if (idMedico == null) {
            return;
        }
        medicoRepository.bloquearPorId(idMedico)
                .orElseThrow(() -> new RecursoNoEncontradoException("Médico no encontrado"));
    }

    @Transactional
    public CitaDTO crear(CitaDTO dto) {
        bloquearMedico(dto.getIdMedico());
        validarAgenda(dto, null, true);
        Cita entity = convertToEntity(dto);
        Cita guardada = citaRepository.save(entity);
        historialService.registrar("CREAR", "Cita",
                "Se agendó la cita" + descripcion(guardada.getIdCita()));
        return convertToDTO(guardada);
    }

    @Transactional
    public CitaDTO modificar(Integer id, CitaDTO dto) {
        bloquearMedico(dto.getIdMedico());
        Cita entity = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada"));

        validarAgenda(dto, id, false);

        entity.setFecha(dto.getFecha());
        entity.setMotivo(dto.getMotivo());
        // El total NO se toma del cliente: lo mantiene DetalleCitaService
        entity.setEstado(dto.getEstado());
        if (dto.getIdMascota() != null)
            entity.setIdMascota(new Mascota(dto.getIdMascota()));
        if (dto.getIdMedico() != null)
            entity.setIdMedico(new Medico(dto.getIdMedico()));
        if (dto.getIdUsuario() != null)
            entity.setIdUsuario(new Usuarios(dto.getIdUsuario()));

        Cita guardada = citaRepository.save(entity);
        historialService.registrar("MODIFICAR", "Cita",
                "Se modificó la cita" + descripcion(guardada.getIdCita()));
        return convertToDTO(guardada);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (consultaRepository.existsByIdCita_IdCita(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar: la cita ya tiene consultas registradas. Use Anular.");
        }
        detalleCitaService.eliminarPorCita(id); // también devuelve su stock al inventario
        String etiqueta = descripcion(id);
        citaRepository.deleteById(id);
        historialService.registrar("ELIMINAR", "Cita", "Se eliminó la cita" + etiqueta);
    }

    // Anular una cita anula también sus detalles y devuelve el stock al inventario
    @Transactional
    public CitaDTO anular(Integer id) {
        if (!citaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Cita no encontrada");
        }
        // Primero los detalles: recalculan el total y la cita se carga después ya
        // actualizada
        detalleCitaService.anularPorCita(id);
        Cita entity = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada"));
        entity.setEstado(false);
        Cita guardada = citaRepository.save(entity);
        historialService.registrar("ANULAR", "Cita",
                "Se anuló la cita" + descripcion(guardada.getIdCita()));
        return convertToDTO(guardada);
    }

    // " de Firulais (20/10/2026 10:30)": la cita descrita por su paciente, sin mostrar ids
    private String descripcion(Integer idCita) {
        return Objects.toString(historialService.describirCita(idCita), "");
    }

    public HojaCitaDTO hoja(Integer id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada"));

        HojaCitaDTO hoja = new HojaCitaDTO();
        hoja.setIdCita(cita.getIdCita());
        hoja.setFecha(cita.getFecha());
        hoja.setMotivo(cita.getMotivo());

        if (cita.getIdMascota() != null) {
            Mascota mascota = cita.getIdMascota();
            hoja.setNombreMascota(mascota.getNombre());
            hoja.setEspecieMascota(mascota.getEspecie());
            if (mascota.getIdDueno() != null) {
                hoja.setNombreDueno(
                        mascota.getIdDueno().getNombre() + " " + mascota.getIdDueno().getApellido());
                hoja.setTelefonoDueno(mascota.getIdDueno().getTelefono());
            }
        }

        if (cita.getIdMedico() != null) {
            hoja.setNombreMedico(
                    cita.getIdMedico().getNombre() + " " + cita.getIdMedico().getApellido());
            hoja.setEspecialidadMedico(cita.getIdMedico().getEspecialidad());
        }

        consultaRepository.findByIdCita_IdCita(id).stream()
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .findFirst()
                .ifPresent(c -> {
                    hoja.setSintomas(c.getSintomas());
                    hoja.setDiagnostico(c.getDiagnostico());
                });

        List<HojaLineaDTO> lineas = detalleCitaRepository.findByIdCita_IdCita(id).stream()
                .filter(d -> Boolean.TRUE.equals(d.getEstado()))
                .map(d -> {
                    HojaLineaDTO linea = new HojaLineaDTO();
                    int cantidad = d.getCantidad() != null ? d.getCantidad() : 1;
                    if (d.getIdMedicamento() != null) {
                        linea.setTipo("Medicamento");
                        linea.setDescripcion(d.getIdMedicamento().getNombre());
                    } else if (d.getIdTratamiento() != null) {
                        linea.setTipo("Tratamiento");
                        linea.setDescripcion(d.getIdTratamiento().getNombre());
                    } else {
                        linea.setTipo("Detalle");
                    }
                    BigDecimal precio = d.getPrecioAplicado() != null ? d.getPrecioAplicado() : BigDecimal.ZERO;
                    linea.setCantidad(cantidad);
                    linea.setPrecioUnitario(precio);
                    linea.setSubtotal(precio.multiply(BigDecimal.valueOf(cantidad)));
                    return linea;
                })
                .collect(Collectors.toList());

        hoja.setLineas(lineas);
        hoja.setTotal(lineas.stream()
                .map(HojaLineaDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return hoja;
    }

    private Cita convertToEntity(CitaDTO dto) {
        Cita entity = new Cita();
        entity.setFecha(dto.getFecha());
        entity.setMotivo(dto.getMotivo());
        entity.setTotal(BigDecimal.ZERO);
        entity.setEstado(dto.getEstado());
        if (dto.getIdMascota() != null)
            entity.setIdMascota(new Mascota(dto.getIdMascota()));
        if (dto.getIdMedico() != null)
            entity.setIdMedico(new Medico(dto.getIdMedico()));
        if (dto.getIdUsuario() != null)
            entity.setIdUsuario(new Usuarios(dto.getIdUsuario()));
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
        dto.setNombreMascota(NombresUtil.mascota(entity.getIdMascota()));
        dto.setNombreMedico(NombresUtil.medico(entity.getIdMedico()));
        dto.setNombreUsuario(NombresUtil.usuario(entity.getIdUsuario()));
        return dto;
    }
}