package com.grupo2.reservas.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupo2.reservas.dto.DisponibilidadDTO;
import com.grupo2.reservas.dto.ReservaDTO;
import com.grupo2.reservas.entity.Reserva;
import com.grupo2.reservas.service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestBody ReservaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.registrarReserva(dto));
    }

    @GetMapping
    public List<Reserva> listar() {
        return reservaService.listarReservas();
    }

    @GetMapping("/{id}")
    public Reserva obtener(@PathVariable Long id) {
        return reservaService.buscarReserva(id);
    }

    @PutMapping("/cancelar/{id}")
    public Reserva cancelar(@PathVariable Long id) {
        return reservaService.cancelarReserva(id);
    }

    @GetMapping("/disponibilidad")
    public DisponibilidadDTO disponibilidad(@RequestParam Long idEspacio,
                                            @RequestParam LocalDate fecha,
                                            @RequestParam LocalTime horaInicio,
                                            @RequestParam LocalTime horaFin) {
        return reservaService.consultarDisponibilidad(idEspacio, fecha, horaInicio, horaFin);
    }
}
