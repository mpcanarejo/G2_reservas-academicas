package com.grupo2.reservas.service;

import com.grupo2.reservas.dto.EspacioDTO;
import com.grupo2.reservas.entity.Espacio;
import com.grupo2.reservas.exception.DatosInvalidosException;
import com.grupo2.reservas.exception.RecursoNoEncontradoException;
import com.grupo2.reservas.exception.ReservaConflictivaException;
import com.grupo2.reservas.repository.EspacioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EspacioService {

    private final EspacioRepository espacioRepository;

    public EspacioService(EspacioRepository espacioRepository) {
        this.espacioRepository = espacioRepository;
    }

    @Transactional(readOnly = true)
    public List<EspacioDTO> listar() {
        return espacioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public EspacioDTO obtenerPorId(Long id) {
        return toDTO(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<EspacioDTO> listarPorTipo(String tipo) {
        return espacioRepository.findByTipoIgnoreCase(tipo).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<EspacioDTO> listarActivos() {
        return espacioRepository.findByActivoTrue().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<EspacioDTO> listarPorCapacidadMinima(Integer capacidad) {
        if (capacidad == null || capacidad < 1) {
            throw new DatosInvalidosException("La capacidad mínima debe ser mayor que cero");
        }
        return espacioRepository.findByCapacidadGreaterThanEqualAndActivoTrue(capacidad)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public EspacioDTO crear(EspacioDTO dto) {
        // Regla: el nombre de un espacio no puede repetirse
        if (espacioRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new DatosInvalidosException("Ya existe un espacio con el nombre: " + dto.getNombre());
        }
        Espacio espacio = new Espacio();
        copiar(dto, espacio);
        espacio.setActivo(dto.getActivo() == null ? Boolean.TRUE : dto.getActivo());
        return toDTO(espacioRepository.save(espacio));
    }

    @Transactional
    public EspacioDTO actualizar(Long id, EspacioDTO dto) {
        Espacio espacio = buscar(id);
        if (espacioRepository.existsByNombreIgnoreCaseAndIdNot(dto.getNombre(), id)) {
            throw new DatosInvalidosException("Ya existe otro espacio con el nombre: " + dto.getNombre());
        }
        copiar(dto, espacio);
        if (dto.getActivo() != null) {
            espacio.setActivo(dto.getActivo());
        }
        return toDTO(espacioRepository.save(espacio));
    }

    @Transactional
    public void eliminar(Long id) {
        Espacio espacio = buscar(id);
        try {
            espacioRepository.delete(espacio);
            espacioRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // Regla: no se elimina un espacio que tiene horarios o reservas asociados
            throw new ReservaConflictivaException(
                    "No se puede eliminar el espacio " + id + " porque tiene horarios o reservas asociados");
        }
    }

    // ---------- métodos auxiliares ----------

    private Espacio buscar(Long id) {
        return espacioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el espacio con id " + id));
    }

    private void copiar(EspacioDTO dto, Espacio espacio) {
        espacio.setNombre(dto.getNombre().trim());
        espacio.setTipo(dto.getTipo().trim().toUpperCase());
        espacio.setCapacidad(dto.getCapacidad());
        espacio.setUbicacion(dto.getUbicacion());
    }

    private EspacioDTO toDTO(Espacio e) {
        EspacioDTO dto = new EspacioDTO();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setTipo(e.getTipo());
        dto.setCapacidad(e.getCapacidad());
        dto.setUbicacion(e.getUbicacion());
        dto.setActivo(e.getActivo());
        return dto;
    }
}