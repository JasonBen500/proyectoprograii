package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.exception.RecursoNoEncontradoException;

import com.proyecto.veterinaria.dto.MascotaDTO;
import com.proyecto.veterinaria.entity.Mascota;
import com.proyecto.veterinaria.entity.Dueno;
import com.proyecto.veterinaria.repository.MascotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final HistorialService historialService;

    public MascotaService(MascotaRepository mascotaRepository, HistorialService historialService) {
        this.mascotaRepository = mascotaRepository;
        this.historialService = historialService;
    }

    public List<MascotaDTO> filtroActivas() {
        return mascotaRepository.findByEstadoTrueOrderByIdMascotaDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MascotaDTO> filtroIdDueno(Integer idDueno) {
        return mascotaRepository.findByIdDueno_IdDueno(idDueno)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MascotaDTO> filtroEspecie(String especie) {
        return mascotaRepository.findByEspecieIgnoreCase(especie)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MascotaDTO> filtroNombre(String nombre) {
        return mascotaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MascotaDTO> findAll() {
        return mascotaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MascotaDTO findById(Integer id) {
        Mascota entity = mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada"));
        return convertToDTO(entity);
    }

    public MascotaDTO agregar(MascotaDTO dto) {
        Mascota entity = mascotaRepository.save(convertToEntity(dto));
        historialService.registrar("CREAR", "Mascota",
                "Se agregó la mascota " + entity.getNombre() + " (" + entity.getEspecie() + ")");
        return convertToDTO(entity);
    }

    public MascotaDTO modificar(Integer id, MascotaDTO dto) {
        Mascota entity = mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada"));

        entity.setNombre(dto.getNombre());
        entity.setEspecie(dto.getEspecie());
        entity.setEdad(dto.getEdad());
        entity.setPeso(dto.getPeso());
        entity.setEstado(dto.getEstado());
        if (dto.getIdDueno() != null)
            entity.setIdDueno(new Dueno(dto.getIdDueno()));

        Mascota guardada = mascotaRepository.save(entity);
        historialService.registrar("MODIFICAR", "Mascota",
                "Se modificó la mascota " + guardada.getNombre());
        return convertToDTO(guardada);
    }

    public MascotaDTO anular(Integer id) {
        Mascota entity = mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada"));
        entity.setEstado(false);
        Mascota guardada = mascotaRepository.save(entity);
        historialService.registrar("ANULAR", "Mascota",
                "Se anuló la mascota " + guardada.getNombre());
        return convertToDTO(guardada);
    }

    private Mascota convertToEntity(MascotaDTO dto) {
        Mascota entity = new Mascota();
        entity.setNombre(dto.getNombre());
        entity.setEspecie(dto.getEspecie());
        entity.setEdad(dto.getEdad());
        entity.setPeso(dto.getPeso());
        entity.setEstado(dto.getEstado());
        if (dto.getIdDueno() != null)
            entity.setIdDueno(new Dueno(dto.getIdDueno()));
        return entity;
    }

    private MascotaDTO convertToDTO(Mascota entity) {
        MascotaDTO dto = new MascotaDTO();
        dto.setIdMascota(entity.getIdMascota());
        dto.setNombre(entity.getNombre());
        dto.setEspecie(entity.getEspecie());
        dto.setEdad(entity.getEdad());
        dto.setPeso(entity.getPeso());
        dto.setEstado(entity.getEstado());
        dto.setIdDueno(entity.getIdDueno() != null ? entity.getIdDueno().getIdDueno() : null);
        dto.setNombreDueno(NombresUtil.dueno(entity.getIdDueno()));
        return dto;
    }
}