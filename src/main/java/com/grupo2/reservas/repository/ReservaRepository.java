package com.grupo2.reservas.repository;
import com.grupo2.reservas.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    boolean existsByEspacio_IdEspacioAndFechaReservaAndEstadoAndHora_inicioLessThanAndHora_finGreaterThan(
        Integer idEspacio,
        java.time.LocalDate fechaReserva,
        String estado,
        String horaFin,
        String horaInicio
);
}

