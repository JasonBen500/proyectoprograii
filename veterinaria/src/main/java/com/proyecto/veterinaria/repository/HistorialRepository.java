package com.proyecto.veterinaria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Historial;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Integer> {
    List<Historial> findTop200ByOrderByIdHistorialDesc();
    List<Historial> findTop5ByOrderByIdHistorialDesc();
}