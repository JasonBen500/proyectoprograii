package com.proyecto.veterinaria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Mascota;

@Repository 
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
    List<Mascota> findByEstadoTrueOrderByIdMascotaDesc();
    List<Mascota> findByIdDueno_IdDueno(Integer idDueno);
    List<Mascota> findByEspecieIgnoreCase(String especie);
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);
}
