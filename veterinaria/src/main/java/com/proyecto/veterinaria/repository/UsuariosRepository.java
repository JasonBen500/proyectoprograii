package com.proyecto.veterinaria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Usuarios;

@Repository 
public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {
    List<Usuarios> findByEstadoTrueOrderByIdUsuarioDesc();
    boolean existsByUsuario(String usuario);
    boolean existsByCorreo(String correo);
}