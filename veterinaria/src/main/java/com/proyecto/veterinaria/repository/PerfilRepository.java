package com.proyecto.veterinaria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Perfil;

@Repository 
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {
    List<Perfil> findByEstadoTrueOrderByIdPerfilDesc();
    Optional<Perfil> findByNombrePerfilIgnoreCase(String nombrePerfil);
    boolean existsByNombrePerfilIgnoreCase(String nombrePerfil);
}
