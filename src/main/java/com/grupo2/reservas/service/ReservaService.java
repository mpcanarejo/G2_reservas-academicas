
package com.grupo2.reservas.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.grupo2.reservas.dto.DisponibilidadDTO;
import com.grupo2.reservas.dto.ReservaDTO;
import com.grupo2.reservas.entity.Reserva;
import com.grupo2.reservas.entity.Usuario;
import com.grupo2.reservas.entity.Espacio;
import com.grupo2.reservas.repository.ReservaRepository;
import com.grupo2.reservas.repository.UsuarioRepository;
import com.grupo2.reservas.repository.EspacioRepository;

import jakarta.transaction.Transactional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;

    // Inyeccion de repositorios
    public ReservaService(
            ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            EspacioRepository espacioRepository) {

        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.espacioRepository = espacioRepository;
    }

    // 1. Registrar una reserva
    @Transactional
    public Reserva registrarReserva(ReservaDTO dto) {

        validarDatos(dto);

        // Verificar disponibilidad
        DisponibilidadDTO disponibilidad = consultarDisponibilidad(
                dto.getIdEspacio(),
                dto.getFechaReserva(),
                dto.getHora_inicio(),
                dto.getHora_fin()
        );

        if (!disponibilidad.getDisponible()) {
            throw new RuntimeException(
                    "El espacio no se encuentra disponible"
            );
        }

        // Buscar usuario
        Usuario usuario = usuarioRepository.findById(
                dto.getIdUsuario().longValue()
        ).orElseThrow(() -> new RuntimeException(
                "Usuario no encontrado"
        ));

        // Buscar espacio
        Espacio espacio = espacioRepository.findById(
                dto.getIdEspacio().longValue()
        ).orElseThrow(() -> new RuntimeException(
                "Espacio no encontrado"
        ));

        // Crear reserva
        Reserva reserva = new Reserva();

        reserva.setUsuario(usuario);
        reserva.setEspacio(espacio);
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setHoraInicio(dto.getHora_inicio());
        reserva.setHoraFin(dto.getHora_fin());
        reserva.setEstado("ACTIVA");

        return reservaRepository.save(reserva);
    }

    // 2. Listar todas las reservas
    @Transactional
    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    // 3. Buscar reserva por ID
    @Transactional
    public Reserva buscarReserva(Long id) {

        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Reserva no encontrada"
                ));
    }

    // 4. Consultar disponibilidad
    @Transactional
    public DisponibilidadDTO consultarDisponibilidad(
            Integer idEspacio,
            LocalDate fecha,
            String horaInicio,
            String horaFin) {

        validarHorario(horaInicio, horaFin);

        boolean ocupada = reservaRepository
                .existsByEspacio_IdEspacioAndFechaReservaAndEstadoAndHora_inicioLessThanAndHora_finGreaterThan(
                        idEspacio,
                        fecha,
                        "ACTIVA",
                        horaFin,
                        horaInicio
                );

        DisponibilidadDTO disponibilidad = new DisponibilidadDTO();

        disponibilidad.setIdEspacio(idEspacio);
        disponibilidad.setFecha(fecha);
        disponibilidad.setHoraInicio(horaInicio);
        disponibilidad.setHoraFin(horaFin);
        disponibilidad.setDisponible(!ocupada);

        return disponibilidad;
    }

    // 5. Cancelar reserva
    @Transactional
    public Reserva cancelarReserva(Long id) {

        Reserva reserva = buscarReserva(id);

        if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
            throw new RuntimeException(
                    "La reserva ya se encuentra cancelada"
            );
        }

        reserva.setEstado("CANCELADA");

        return reservaRepository.save(reserva);
    }

    // 6. Validar datos
    private void validarDatos(ReservaDTO dto) {

        if (dto == null ||
                dto.getIdUsuario() == null ||
                dto.getIdEspacio() == null ||
                dto.getFechaReserva() == null ||
                dto.getHora_inicio() == null ||
                dto.getHora_fin() == null) {

            throw new RuntimeException(
                    "Todos los datos de la reserva son obligatorios"
            );
        }

        validarHorario(dto.getHora_inicio(), dto.getHora_fin());
    }

    // 7. Validar horarios
    private void validarHorario(String inicio, String fin) {

        try {

            LocalTime horaInicio = LocalTime.parse(inicio);
            LocalTime horaFin = LocalTime.parse(fin);

            if (!horaInicio.isBefore(horaFin)) {
                throw new RuntimeException(
                        "La hora de inicio debe ser menor que la hora final"
                );
            }

        } catch (java.time.format.DateTimeParseException e) {

            throw new RuntimeException(
                    "El formato de hora debe ser HH:mm"
            );
        }
    }
}

