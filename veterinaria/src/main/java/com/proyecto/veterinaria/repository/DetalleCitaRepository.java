package com.proyecto.veterinaria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.DetalleCita;

@Repository
public interface DetalleCitaRepository extends JpaRepository<DetalleCita, Integer> {
    List<DetalleCita> findByEstadoTrueOrderByIdDetalleCitaDesc();

    List<DetalleCita> findByIdCita_IdCita(Integer idCita);

    List<DetalleCita> findByIdMedicamento_IdMedicamento(Integer idMedicamento);

    List<DetalleCita> findByIdTratamiento_IdTratamiento(Integer idTratamiento);
}
