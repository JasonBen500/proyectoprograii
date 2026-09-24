package com.proyecto.veterinaria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Consulta;

@Repository 
public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
    List<Consulta> findByEstadoTrueOrderByIdConsultaDesc();
    List<Consulta> findByIdCita_IdCita(Integer idCita);
    List<Consulta> findByDiagnosticoContainingIgnoreCase(String diagnostico);
}
