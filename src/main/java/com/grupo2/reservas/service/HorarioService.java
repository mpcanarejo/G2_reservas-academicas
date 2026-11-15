package com.grupo2.reservas.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.grupo2.reservas.dto.HorarioDTO;
import com.grupo2.reservas.entity.Espacio;
import com.grupo2.reservas.entity.Horario;
import com.grupo2.reservas.exception.DatosInvalidosException;
import com.grupo2.reservas.exception.RecursoNoEncontradoException;
import com.grupo2.reservas.exception.ReservaConflictivaException;
import com.grupo2.reservas.repository.EspacioRepository;
import com.grupo2.reservas.repository.HorarioRepository;

@Service
public class HorarioService {

    private static final Set<String> DIAS = Set.of(
            "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO");

    private final HorarioRepository horarioRepository;
    private final EspacioRepository espacioRepository;
    private final EntityManager entityManager;

    public HorarioService(HorarioRepository horarioRepository,
                          EspacioRepository espacioRepository,
                          EntityManager entityManager) {
        this.horarioRepository = horarioRepository;
        this.espacioRepository = espacioRepository;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<HorarioDTO> obtenerTodos() {
        return horarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<HorarioDTO> obtenerPorEspacio(Long espacioId) {
        buscarEspacio(espacioId);
        return horarioRepository.findByEspacioId(espacioId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public HorarioDTO obtenerPorId(Long id) {
        return toDTO(buscar(id));
    }

    @Transactional
    public HorarioDTO guardar(HorarioDTO dto) {
        validarHorario(dto);
        Espacio espacio = bloquearEspacio(dto.getEspacioId());
        String dia = normalizarDia(dto.getDiaSemana());
        validarSolapamiento(dto, dia, null);
        Horario horario = new Horario();
        copiar(dto, dia, espacio, horario);
        return toDTO(horarioRepository.save(horario));
    }

    @Transactional
    public HorarioDTO actualizar(Long id, HorarioDTO dto) {
        validarHorario(dto);
        Horario horario = buscar(id);
        Espacio espacio = bloquearEspacio(dto.getEspacioId());
        String dia = normalizarDia(dto.getDiaSemana());
        validarSolapamiento(dto, dia, id);
        copiar(dto, dia, espacio, horario);
        return toDTO(horarioRepository.save(horario));
    }

    @Transactional
    public void eliminar(Long id) {
        Horario horario = buscar(id);
        bloquearEspacio(horario.getEspacio().getId());
        horarioRepository.delete(horario);
    }

    private void validarHorario(HorarioDTO dto) {
        if (dto == null || dto.getEspacioId() == null || dto.getEspacioId() < 1
                || dto.getDiaSemana() == null || dto.getDiaSemana().isBlank()
                || dto.getHoraInicio() == null || dto.getHoraFin() == null) {
            throw new DatosInvalidosException("Debe indicar espacio, día y horas del horario");
        }
        if (!DIAS.contains(normalizarDia(dto.getDiaSemana()))) {
            throw new DatosInvalidosException("El día de la semana no es válido");
        }
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
            throw new DatosInvalidosException("La hora de inicio debe ser menor que la hora de fin");
        }
    }

    private void validarSolapamiento(HorarioDTO dto, String dia, Long idActual) {
        boolean superpuesto = horarioRepository.findByEspacioId(dto.getEspacioId()).stream()
                .filter(h -> !h.getId().equals(idActual))
                .filter(h -> normalizarDia(h.getDiaSemana()).equals(dia))
                .anyMatch(h -> h.getHoraInicio().isBefore(dto.getHoraFin())
                        && h.getHoraFin().isAfter(dto.getHoraInicio()));
        if (superpuesto) {
            throw new ReservaConflictivaException(
                    "El espacio ya tiene un horario que se superpone en ese día");
        }
    }

    private String normalizarDia(String dia) {
        return Normalizer.normalize(dia.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);
    }

    private Horario buscar(Long id) {
        if (id == null || id < 1) {
            throw new DatosInvalidosException("El identificador del horario debe ser positivo");
        }
        return horarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Horario no encontrado con id " + id));
    }

    private Espacio buscarEspacio(Long id) {
        if (id == null || id < 1) {
            throw new DatosInvalidosException("El identificador del espacio debe ser positivo");
        }
        return espacioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espacio no encontrado con id " + id));
    }

    private Espacio bloquearEspacio(Long id) {
        Espacio espacio = buscarEspacio(id);
        entityManager.refresh(espacio, LockModeType.PESSIMISTIC_WRITE);
        return espacio;
    }

    private void copiar(HorarioDTO dto, String dia, Espacio espacio, Horario horario) {
        horario.setEspacio(espacio);
        horario.setDiaSemana(dia);
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
    }

    private HorarioDTO toDTO(Horario horario) {
        HorarioDTO dto = new HorarioDTO();
        dto.setId(horario.getId());
        dto.setEspacioId(horario.getEspacio().getId());
        dto.setDiaSemana(horario.getDiaSemana());
        dto.setHoraInicio(horario.getHoraInicio());
        dto.setHoraFin(horario.getHoraFin());
        return dto;
    }
}