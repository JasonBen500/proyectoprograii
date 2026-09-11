package com.proyecto.veterinaria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Mascota;

@Repository 
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
}
