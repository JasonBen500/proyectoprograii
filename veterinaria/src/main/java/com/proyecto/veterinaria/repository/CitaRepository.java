package com.proyecto.veterinaria.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Cita;

@Repository 
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    List<Cita> findByEstadoTrueOrderByIdCitaDesc();
    List<Cita> findByIdMascota_IdMascota(Integer idMascota);
    List<Cita> findByIdMedico_IdMedico(Integer idMedico);
    List<Cita> findByFechaBetween(Date fechaInicio, Date fechaFin);
}
