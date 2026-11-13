package com.grupo2.reservas.repository;

import com.grupo2.reservas.entity.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Long> {

    List<Espacio> findByTipoIgnoreCase(String tipo);

    List<Espacio> findByActivoTrue();

    List<Espacio> findByCapacidadGreaterThanEqualAndActivoTrue(Integer capacidad);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
