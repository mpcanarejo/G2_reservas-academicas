package com.grupo2.reservas.service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.grupo2.reservas.dto.DisponibilidadDTO;
import com.grupo2.reservas.dto.ReservaDTO;
import com.grupo2.reservas.entity.Espacio;
import com.grupo2.reservas.entity.Reserva;
import com.grupo2.reservas.entity.Usuario;
import com.grupo2.reservas.exception.DatosInvalidosException;
import com.grupo2.reservas.exception.RecursoNoEncontradoException;
import com.grupo2.reservas.exception.ReservaConflictivaException;
import com.grupo2.reservas.repository.EspacioRepository;
import com.grupo2.reservas.repository.HorarioRepository;
import com.grupo2.reservas.repository.ReservaRepository;
import com.grupo2.reservas.repository.UsuarioRepository;

@Service
public class ReservaService {

    private static final String ESTADO_ACTIVA = "ACTIVA";
    private static final String ESTADO_CANCELADA = "CANCELADA";

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;
    private final HorarioRepository horarioRepository;
    private final EntityManager entityManager;

    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioRepository usuarioRepository,
                          EspacioRepository espacioRepository,
                          HorarioRepository horarioRepository,
                          EntityManager entityManager) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.espacioRepository = espacioRepository;
        this.horarioRepository = horarioRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public Reserva registrarReserva(ReservaDTO dto) {
        validarDatos(dto);
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id " + dto.getIdUsuario()));
        Espacio espacio = buscarEspacio(dto.getIdEspacio());
        entityManager.refresh(espacio, LockModeType.PESSIMISTIC_WRITE);
        if (!estaDisponible(espacio, dto.getFechaReserva(), dto.getHoraInicio(), dto.getHoraFin())) {
            throw new ReservaConflictivaException(
                    "El espacio está inactivo, fuera del horario habilitado o ya está reservado");
        }
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setEspacio(espacio);
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setHoraInicio(dto.getHoraInicio());
        reserva.setHoraFin(dto.getHoraFin());
        reserva.setMotivo(dto.getMotivo() == null ? null : dto.getMotivo().trim());
        reserva.setEstado(ESTADO_ACTIVA);
        return reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Reserva buscarReserva(Long id) {
        if (id == null || id < 1) {
            throw new DatosInvalidosException("El identificador de la reserva debe ser positivo");
        }
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con id " + id));
    }

    @Transactional(readOnly = true)
    public DisponibilidadDTO consultarDisponibilidad(Long idEspacio, LocalDate fecha,
                                                     LocalTime horaInicio, LocalTime horaFin) {
        validarFechaHorario(fecha, horaInicio, horaFin);
        Espacio espacio = buscarEspacio(idEspacio);
        DisponibilidadDTO disponibilidad = new DisponibilidadDTO();
        disponibilidad.setIdEspacio(idEspacio);
        disponibilidad.setFecha(fecha);
        disponibilidad.setHoraInicio(horaInicio);
        disponibilidad.setHoraFin(horaFin);
        disponibilidad.setDisponible(estaDisponible(espacio, fecha, horaInicio, horaFin));
        return disponibilidad;
    }

    @Transactional
    public Reserva cancelarReserva(Long id) {
        Reserva reserva = buscarReserva(id);
        entityManager.refresh(reserva, LockModeType.PESSIMISTIC_WRITE);
        if (ESTADO_CANCELADA.equalsIgnoreCase(reserva.getEstado())) {
            throw new DatosInvalidosException("La reserva ya se encuentra cancelada");
        }
        reserva.setEstado(ESTADO_CANCELADA);
        return reservaRepository.save(reserva);
    }

    private boolean estaDisponible(Espacio espacio, LocalDate fecha,
                                    LocalTime horaInicio, LocalTime horaFin) {
        if (!Boolean.TRUE.equals(espacio.getActivo())) {
            return false;
        }
        String dia = normalizarDia(fecha.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")));
        boolean habilitado = horarioRepository.findByEspacioId(espacio.getId()).stream()
                .anyMatch(h -> normalizarDia(h.getDiaSemana()).equals(dia)
                        && !horaInicio.isBefore(h.getHoraInicio())
                        && !horaFin.isAfter(h.getHoraFin()));
        if (!habilitado) {
            return false;
        }
        return !reservaRepository
                .existsByEspacio_IdAndFechaReservaAndEstadoNotAndHoraInicioLessThanAndHoraFinGreaterThan(
                        espacio.getId(), fecha, ESTADO_CANCELADA, horaFin, horaInicio);
    }

    private String normalizarDia(String dia) {
        return Normalizer.normalize(dia.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);
    }

    private Espacio buscarEspacio(Long id) {
        if (id == null || id < 1) {
            throw new DatosInvalidosException("El identificador del espacio debe ser positivo");
        }
        return espacioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espacio no encontrado con id " + id));
    }

    private void validarDatos(ReservaDTO dto) {
        if (dto == null || dto.getIdUsuario() == null || dto.getIdUsuario() < 1
                || dto.getIdEspacio() == null || dto.getIdEspacio() < 1) {
            throw new DatosInvalidosException("Debe indicar identificadores positivos de usuario y espacio");
        }
        if (dto.getMotivo() != null && dto.getMotivo().length() > 255) {
            throw new DatosInvalidosException("El motivo no puede superar 255 caracteres");
        }
        validarFechaHorario(dto.getFechaReserva(), dto.getHoraInicio(), dto.getHoraFin());
    }

    private void validarFechaHorario(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        if (fecha == null || horaInicio == null || horaFin == null) {
            throw new DatosInvalidosException("La fecha y las horas de inicio y fin son obligatorias");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new DatosInvalidosException("No se pueden consultar ni crear reservas en fechas pasadas");
        }
        if (!horaInicio.isBefore(horaFin)) {
            throw new DatosInvalidosException("La hora de inicio debe ser menor que la hora final");
        }
    }
}