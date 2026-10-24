package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.exception.RecursoNoEncontradoException;

import com.proyecto.veterinaria.dto.DuenoDTO;
import com.proyecto.veterinaria.entity.Dueno;
import com.proyecto.veterinaria.repository.DuenoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DuenoService {

    private final DuenoRepository duenoRepository;
    private final HistorialService historialService;

    public DuenoService(DuenoRepository duenoRepository, HistorialService historialService) {
        this.duenoRepository = duenoRepository;
        this.historialService = historialService;
    }

    public List<DuenoDTO> findAll() {
        return duenoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DuenoDTO> findActivos() {
        return duenoRepository.findByEstadoTrueOrderByIdDuenoDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DuenoDTO> buscarPorNombreOApellido(String texto) {
        return duenoRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(texto, texto)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // BUSCAR EL TELEFONO DEL DUEÑO//
    public boolean existeTelefono(String telefono) {
        return duenoRepository.existsByTelefono(telefono);
    }

    // BUSCAR POR NOMBRE Y APELLIDO JUNTOS PARA NO HACERLO POR SEPARADO//
    public boolean existeNombreCompleto(String nombre, String apellido) {
        return duenoRepository.existsByNombreIgnoreCaseAndApellidoIgnoreCase(nombre, apellido);
    }

    // BUSCAR ID DEL DUEÑO//
    public DuenoDTO findById(Integer id) {
        Dueno entity = duenoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dueno no encontrado"));
        return convertToDTO(entity);
    }

    public DuenoDTO crear(DuenoDTO dto) {
        Dueno entity = duenoRepository.save(convertToEntity(dto));
        historialService.registrar("CREAR", "Dueño",
                "Se agregó el dueño " + entity.getNombre() + " " + entity.getApellido());
        return convertToDTO(entity);
    }

    public DuenoDTO actualizar(Integer id, DuenoDTO dto) {
        Dueno entity = duenoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dueno no encontrado"));

        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setTelefono(dto.getTelefono());
        entity.setDireccion(dto.getDireccion());
        entity.setEstado(dto.getEstado());

        Dueno guardado = duenoRepository.save(entity);
        historialService.registrar("MODIFICAR", "Dueño",
                "Se modificó el dueño " + guardado.getNombre() + " " + guardado.getApellido());
        return convertToDTO(guardado);
    }

    public DuenoDTO anular(Integer id) {
        Dueno entity = duenoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dueno no encontrado"));
        entity.setEstado(false);
        Dueno guardado = duenoRepository.save(entity);
        historialService.registrar("ANULAR", "Dueño",
                "Se anuló el dueño " + guardado.getNombre() + " " + guardado.getApellido());
        return convertToDTO(guardado);
    }

    private Dueno convertToEntity(DuenoDTO dto) {
        Dueno entity = new Dueno();
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setTelefono(dto.getTelefono());
        entity.setDireccion(dto.getDireccion());
        entity.setEstado(dto.getEstado());
        return entity;
    }

    private DuenoDTO convertToDTO(Dueno entity) {
        DuenoDTO dto = new DuenoDTO();
        dto.setIdDueno(entity.getIdDueno());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setTelefono(entity.getTelefono());
        dto.setDireccion(entity.getDireccion());
        dto.setEstado(entity.getEstado());
        return dto;
    }
}