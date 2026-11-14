package com.grupo2.reservas.service;

import com.grupo2.reservas.dto.EspacioDTO;
import com.grupo2.reservas.entity.Espacio;
import com.grupo2.reservas.exception.DatosInvalidosException;
import com.grupo2.reservas.exception.ReservaConflictivaException;
import com.grupo2.reservas.repository.EspacioRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EspacioServiceTest {
    private final EspacioRepository repository = mock(EspacioRepository.class);
    private final EspacioService service = new EspacioService(repository);

    @Test
    void rechazaNombreDuplicadoConEspacios() {
        EspacioDTO dto = new EspacioDTO();
        dto.setNombre(" Aula 1 ");
        when(repository.existsByNombreIgnoreCase("Aula 1")).thenReturn(true);
        assertThrows(DatosInvalidosException.class, () -> service.crear(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void rechazaNombreDuplicadoAlActualizar() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Espacio()));
        when(repository.existsByNombreIgnoreCaseAndIdNot("Aula 1", 1L)).thenReturn(true);
        EspacioDTO dto = new EspacioDTO();
        dto.setNombre(" Aula 1 ");
        assertThrows(DatosInvalidosException.class, () -> service.actualizar(1L, dto));
        verify(repository, never()).save(any());
    }

    @Test
    void impideEliminarEspacioConHorarios() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Espacio()));
        when(repository.contarHorarios(1L)).thenReturn(1L);
        assertThrows(ReservaConflictivaException.class, () -> service.eliminar(1L));
        verify(repository, never()).delete(any());
    }

    @Test
    void impideEliminarEspacioConReservas() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Espacio()));
        when(repository.contarReservas(1L)).thenReturn(1L);
        assertThrows(ReservaConflictivaException.class, () -> service.eliminar(1L));
        verify(repository, never()).delete(any());
    }

    @Test
    void eliminaEspacioSinRelaciones() {
        Espacio espacio = new Espacio();
        when(repository.findById(1L)).thenReturn(Optional.of(espacio));
        service.eliminar(1L);
        verify(repository).delete(espacio);
        verify(repository).flush();
    }
}
