package com.grupo2.reservas.service;

import com.grupo2.reservas.entity.Espacio;
import com.grupo2.reservas.entity.Horario;
import com.grupo2.reservas.exception.DatosInvalidosException;
import com.grupo2.reservas.exception.RecursoNoEncontradoException;
import com.grupo2.reservas.repository.EspacioRepository;
import com.grupo2.reservas.repository.HorarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final EspacioRepository espacioRepository;

    public HorarioService(HorarioRepository horarioRepository,
                          EspacioRepository espacioRepository) {
        this.horarioRepository = horarioRepository;
        this.espacioRepository = espacioRepository;
    }

    public List<Horario> obtenerTodos() {
        return horarioRepository.findAll();
    }

    public List<Horario> obtenerPorEspacio(Long espacioId) {
        return horarioRepository.findByEspacioId(espacioId);
    }

    public Horario obtenerPorId(Long id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Horario no encontrado con id " + id));
    }

    public Horario guardar(Horario horario) {
        horario.setEspacio(buscarEspacio(horario.getEspacio()));
        return horarioRepository.save(horario);
    }

    public Horario actualizar(Long id, Horario datos) {
        Horario existente = obtenerPorId(id);
        existente.setEspacio(buscarEspacio(datos.getEspacio()));
        existente.setDiaSemana(datos.getDiaSemana());
        existente.setHoraInicio(datos.getHoraInicio());
        existente.setHoraFin(datos.getHoraFin());
        return horarioRepository.save(existente);
    }

    public void eliminar(Long id) {
        horarioRepository.delete(obtenerPorId(id));
    }

    private Espacio buscarEspacio(Espacio espacio) {
        if (espacio == null || espacio.getId() == null) {
            throw new DatosInvalidosException(
                    "Debe indicar el espacio del horario (campo espacio.id)");
        }
        return espacioRepository.findById(espacio.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Espacio no encontrado con id " + espacio.getId()));
    }
}