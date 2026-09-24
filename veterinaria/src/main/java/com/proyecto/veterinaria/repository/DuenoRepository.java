package com.proyecto.veterinaria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Dueno;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Integer> {
    List<Dueno> findByEstadoTrueOrderByIdDuenoDesc();

    List<Dueno> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    boolean existsByTelefono(String telefono);

    boolean existsByNombreIgnoreCaseAndApellidoIgnoreCase(String nombre, String apellido);
}
