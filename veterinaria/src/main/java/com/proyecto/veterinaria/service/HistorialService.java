package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.dto.HistorialDTO;
import com.proyecto.veterinaria.entity.Historial;
import com.proyecto.veterinaria.repository.CitaRepository;
import com.proyecto.veterinaria.repository.HistorialRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialService {

    private final HistorialRepository historialRepository;
    private final CitaRepository citaRepository;

    public HistorialService(HistorialRepository historialRepository, CitaRepository citaRepository) {
        this.historialRepository = historialRepository;
        this.citaRepository = citaRepository;
    }

    // Describe una cita por su paciente y fecha, por ejemplo " de Firulais (20/10/2026 10:30)".
    // Devuelve "" si no se puede (así el movimiento se registra igual).
    public String describirCita(Integer idCita) {
        if (idCita == null) {
            return "";
        }
        return citaRepository.findById(idCita).map(cita -> {
            String mascota = NombresUtil.mascota(cita.getIdMascota());
            String fecha = cita.getFecha() == null
                    ? ""
                    : " (" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(cita.getFecha()) + ")";
            return (mascota != null ? " de " + mascota : "") + fecha;
        }).orElse("");
    }

    // accion: CREAR, MODIFICAR, ANULAR, ELIMINAR, CONSULTA
    // Se llama al final del método de negocio, dentro de su misma transacción
    public void registrar(String accion, String entidad, String descripcion) {
        Historial h = new Historial();
        h.setFecha(new Date());
        h.setUsuario(usuarioActual());
        h.setAccion(accion);
        h.setEntidad(entidad);
        h.setDescripcion(descripcion != null && descripcion.length() > 255
                ? descripcion.substring(0, 255)
                : descripcion);
        historialRepository.save(h);
    }

    public List<HistorialDTO> recientes() {
        return historialRepository.findTop200ByOrderByIdHistorialDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<HistorialDTO> ultimos() {
        return historialRepository.findTop5ByOrderByIdHistorialDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : auth.getName();
    }

    private HistorialDTO convertToDTO(Historial entity) {
        HistorialDTO dto = new HistorialDTO();
        dto.setIdHistorial(entity.getIdHistorial());
        dto.setFecha(entity.getFecha());
        dto.setUsuario(entity.getUsuario());
        dto.setAccion(entity.getAccion());
        dto.setEntidad(entity.getEntidad());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }
}