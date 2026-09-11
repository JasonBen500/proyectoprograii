package com.proyecto.veterinaria.service;

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

    public MascotaService(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    public List<MascotaDTO> findAll() {
        return mascotaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MascotaDTO findById(Integer id) {
        Mascota entity = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        return convertToDTO(entity);
    }

    public MascotaDTO agregar(MascotaDTO dto) {
        Mascota entity = convertToEntity(dto);
        return convertToDTO(mascotaRepository.save(entity));
    }

    public MascotaDTO modificar(Integer id, MascotaDTO dto) {
        Mascota entity = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        entity.setNombre(dto.getNombre());
        entity.setEspecie(dto.getEspecie());
        entity.setEdad(dto.getEdad());
        entity.setPeso(dto.getPeso());
        entity.setEstado(dto.getEstado());
        if (dto.getIdDueno() != null) entity.setIdDueno(new Dueno(dto.getIdDueno()));

        return convertToDTO(mascotaRepository.save(entity));
    }

    public void eliminar(Integer id) {
        mascotaRepository.deleteById(id);
    }

    public MascotaDTO anular(Integer id) {
        Mascota entity = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        entity.setEstado(false);
        return convertToDTO(mascotaRepository.save(entity));
    }

    private Mascota convertToEntity(MascotaDTO dto) {
        Mascota entity = new Mascota();
        entity.setNombre(dto.getNombre());
        entity.setEspecie(dto.getEspecie());
        entity.setEdad(dto.getEdad());
        entity.setPeso(dto.getPeso());
        entity.setEstado(dto.getEstado());
        if (dto.getIdDueno() != null) entity.setIdDueno(new Dueno(dto.getIdDueno()));
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
        return dto;
    }
}