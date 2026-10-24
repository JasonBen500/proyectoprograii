package com.proyecto.veterinaria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

import com.proyecto.veterinaria.entity.Medico;

@Repository 
public interface MedicoRepository extends JpaRepository<Medico, Integer> {
    List<Medico> findByEstadoTrueOrderByIdMedicoDesc();
    List<Medico> findByEspecialidadIgnoreCase(String especialidad);
    boolean existsByCedula(String cedula);

    // Bloquea la fila del médico hasta terminar la transacción: sirve para que dos
    // citas simultáneas no se agenden en el mismo horario
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Medico m WHERE m.idMedico = :id")
    Optional<Medico> bloquearPorId(@Param("id") Integer id);
}
