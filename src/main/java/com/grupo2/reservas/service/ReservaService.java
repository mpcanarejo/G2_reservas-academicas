package com.grupo2.reservas.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
import com.grupo2.reservas.repository.ReservaRepository;
import com.grupo2.reservas.repository.UsuarioRepository;

@Service
public class ReservaService {

    private static final String ESTADO_ACTIVA = "ACTIVA";
    private static final String ESTADO_CANCELADA = "CANCELADA";

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioRepository usuarioRepository,
                          EspacioRepository espacioRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.espacioRepository = espacioRepository;
    }

    @Transactional
    public Reserva registrarReserva(ReservaDTO dto) {
        validarDatos(dto);

        DisponibilidadDTO disponibilidad = consultarDisponibilidad(
                dto.getIdEspacio(), dto.getFechaReserva(), dto.getHoraInicio(), dto.getHoraFin());

        if (!disponibilidad.isDisponible()) {
            throw new ReservaConflictivaException(
                    "El espacio no se encuentra disponible en esa fecha y horario");
        }

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id " + dto.getIdUsuario()));

        Espacio espacio = espacioRepository.findById(dto.getIdEspacio())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Espacio no encontrado con id " + dto.getIdEspacio()));

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setEspacio(espacio);
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setHoraInicio(dto.getHoraInicio());
        reserva.setHoraFin(dto.getHoraFin());
        reserva.setMotivo(dto.getMotivo());
        reserva.setEstado(ESTADO_ACTIVA);
        return reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Reserva buscarReserva(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reserva no encontrada con id " + id));
    }

    @Transactional(readOnly = true)
    public DisponibilidadDTO consultarDisponibilidad(Long idEspacio, LocalDate fecha,
                                                     LocalTime horaInicio, LocalTime horaFin) {
        validarHorario(horaInicio, horaFin);

        boolean ocupada = reservaRepository
                .existsByEspacio_IdAndFechaReservaAndEstadoNotAndHoraInicioLessThanAndHoraFinGreaterThan(
                        idEspacio, fecha, ESTADO_CANCELADA, horaFin, horaInicio);

        DisponibilidadDTO disponibilidad = new DisponibilidadDTO();
        disponibilidad.setIdEspacio(idEspacio);
        disponibilidad.setFecha(fecha);
        disponibilidad.setHoraInicio(horaInicio);
        disponibilidad.setHoraFin(horaFin);
        disponibilidad.setDisponible(!ocupada);
        return disponibilidad;
    }

    @Transactional
    public Reserva cancelarReserva(Long id) {
        Reserva reserva = buscarReserva(id);

        if (ESTADO_CANCELADA.equalsIgnoreCase(reserva.getEstado())) {
            throw new DatosInvalidosException("La reserva ya se encuentra cancelada");
        }

        reserva.setEstado(ESTADO_CANCELADA);
        return reservaRepository.save(reserva);
    }

    private void validarDatos(ReservaDTO dto) {
        if (dto == null ||
                dto.getIdUsuario() == null ||
                dto.getIdEspacio() == null ||
                dto.getFechaReserva() == null ||
                dto.getHoraInicio() == null ||
                dto.getHoraFin() == null) {
            throw new DatosInvalidosException("Todos los datos de la reserva son obligatorios");
        }

        if (dto.getFechaReserva().isBefore(LocalDate.now())) {
            throw new DatosInvalidosException("No se pueden crear reservas en fechas pasadas");
        }

        validarHorario(dto.getHoraInicio(), dto.getHoraFin());
    }

    private void validarHorario(LocalTime horaInicio, LocalTime horaFin) {
        if (horaInicio == null || horaFin == null) {
            throw new DatosInvalidosException("Las horas de inicio y fin son obligatorias");
        }
        if (!horaInicio.isBefore(horaFin)) {
            throw new DatosInvalidosException("La hora de inicio debe ser menor que la hora final");
        }
    }
}
