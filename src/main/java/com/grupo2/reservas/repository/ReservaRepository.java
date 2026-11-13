package com.grupo2.reservas.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo2.reservas.entity.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    boolean existsByEspacio_IdAndFechaReservaAndEstadoNotAndHoraInicioLessThanAndHoraFinGreaterThan(
            Long idEspacio,
            LocalDate fecha,
            String estadoNoCancelada,
            LocalTime nuevaHoraFin,
            LocalTime nuevaHoraInicio);

    List<Reserva> findByEspacioIdAndFechaReserva(Long idEspacio, LocalDate fecha);

    List<Reserva> findByUsuarioId(Long idUsuario);
}


