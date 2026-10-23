package com.proyecto.veterinaria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.veterinaria.entity.Medicamentos;

@Repository
public interface MedicamentosRepository extends JpaRepository<Medicamentos, Integer> {
    List<Medicamentos> findByEstadoTrueOrderByIdMedicamentoDesc();

    List<Medicamentos> findByNombreContainingIgnoreCase(String nombre);

    List<Medicamentos> findByStockLessThan(Integer cantidad);

    boolean existsByNombreIgnoreCase(String nombre);

    // evita que dos peticiones simultáneas se pisen el stock
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Medicamentos m SET m.stock = COALESCE(m.stock, 0) + :cambio "
            + "WHERE m.idMedicamento = :id AND COALESCE(m.stock, 0) + :cambio >= 0")
    int ajustarStockAtomico(@Param("id") Integer id, @Param("cambio") int cambio);
}
