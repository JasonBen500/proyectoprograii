package com.proyecto.veterinaria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Cita;

@Repository 
public interface CitaRepository extends JpaRepository <Cita, Integer> {
    
}
