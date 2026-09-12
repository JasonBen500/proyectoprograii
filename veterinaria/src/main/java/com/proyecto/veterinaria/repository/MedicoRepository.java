package com.proyecto.veterinaria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Medico;

@Repository 
public interface MedicoRepository extends JpaRepository<Medico, Integer> {
    List<Medico> findByEstadoTrueOrderByIdMedicoDesc();
    List<Medico> findByEspecialidadIgnoreCase(String especialidad);
    boolean existsByCedula(String cedula);
}
