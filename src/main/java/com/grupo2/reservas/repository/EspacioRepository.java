package com.grupo2.reservas.repository;

import com.grupo2.reservas.entity.Espacio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspacioRepository extends JpaRepository<Espacio, Long> {

    List<Espacio> findByTipo(String tipo);
}