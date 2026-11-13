package com.grupo2.reservas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo2.reservas.dto.HorarioDTO;
import com.grupo2.reservas.entity.Espacio;
import com.grupo2.reservas.entity.Horario;
import com.grupo2.reservas.exception.DatosInvalidosException;
import com.grupo2.reservas.exception.RecursoNoEncontradoException;
import com.grupo2.reservas.repository.EspacioRepository;
import com.grupo2.reservas.repository.HorarioRepository;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final EspacioRepository espacioRepository;

    public HorarioService(HorarioRepository horarioRepository, EspacioRepository espacioRepository) {
        this.horarioRepository = horarioRepository;
        this.espacioRepository = espacioRepository;
    }

    @Transactional(readOnly = true)
    public List<HorarioDTO> obtenerTodos() {
        return horarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<HorarioDTO> obtenerPorEspacio(Long espacioId) {
        return horarioRepository.findByEspacioId(espacioId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public HorarioDTO obtenerPorId(Long id) {
        return toDTO(buscar(id));
    }

    @Transactional
    public HorarioDTO guardar(HorarioDTO dto) {
        validarHorario(dto);
        Horario horario = new Horario();
        horario.setEspacio(buscarEspacio(dto.getEspacioId()));
        horario.setDiaSemana(dto.getDiaSemana().trim().toUpperCase());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        return toDTO(horarioRepository.save(horario));
    }

    @Transactional
    public HorarioDTO actualizar(Long id, HorarioDTO dto) {
        validarHorario(dto);
        Horario horario = buscar(id);
        horario.setEspacio(buscarEspacio(dto.getEspacioId()));
        horario.setDiaSemana(dto.getDiaSemana().trim().toUpperCase());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        return toDTO(horarioRepository.save(horario));
    }

    @Transactional
    public void eliminar(Long id) {
        horarioRepository.delete(buscar(id));
    }

    private void validarHorario(HorarioDTO dto) {
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
            throw new DatosInvalidosException("La hora de inicio debe ser menor que la hora de fin");
        }
    }

    private Horario buscar(Long id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Horario no encontrado con id " + id));
    }

    private Espacio buscarEspacio(Long espacioId) {
        if (espacioId == null) {
            throw new DatosInvalidosException("Debe indicar el espacio del horario (espacioId)");
        }
        return espacioRepository.findById(espacioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espacio no encontrado con id " + espacioId));
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
