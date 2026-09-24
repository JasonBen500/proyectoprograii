package com.proyecto.veterinaria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Medicamentos;

@Repository 
public interface MedicamentosRepository extends JpaRepository<Medicamentos, Integer> {
    List<Medicamentos> findByEstadoTrueOrderByIdMedicamentoDesc();
    List<Medicamentos> findByNombreContainingIgnoreCase(String nombre);
    List<Medicamentos> findByStockLessThan(Integer cantidad);
    boolean existsByNombreIgnoreCase(String nombre);
}
