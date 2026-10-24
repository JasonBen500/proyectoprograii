package com.proyecto.veterinaria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Usuarios;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {
    List<Usuarios> findByEstadoTrueOrderByIdUsuarioDesc();

    Optional<Usuarios> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);

    boolean existsByCorreo(String correo);

    // Para modificar: ignora al propio usuario
    boolean existsByUsuarioAndIdUsuarioNot(String usuario, Integer idUsuario);

    boolean existsByCorreoAndIdUsuarioNot(String correo, Integer idUsuario);
}
