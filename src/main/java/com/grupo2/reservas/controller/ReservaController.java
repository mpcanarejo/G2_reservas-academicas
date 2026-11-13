
package com.grupo2.reservas.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import com.grupo2.reservas.dto.ReservaDTO;
import com.grupo2.reservas.dto.DisponibilidadDTO;
import com.grupo2.reservas.entity.Reserva;
import com.grupo2.reservas.service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    private final ReservaService reservaService;

    // Constructor
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // 1. Registrar una reserva
    @PostMapping
    public Reserva registrarReserva(@RequestBody ReservaDTO dto) {
        return reservaService.registrarReserva(dto);
    }

    // 2. Listar todas las reservas
    @GetMapping
    public List<Reserva> listarReservas() {
        return reservaService.listarReservas();
    }

    // 3. Buscar una reserva por ID
    @GetMapping("/{id}")
    public Reserva buscarReserva(@PathVariable Long id) {
        return reservaService.buscarReserva(id);
    }

    // 4. Cancelar una reserva
    @PutMapping("/cancelar/{id}")
    public Reserva cancelarReserva(@PathVariable Long id) {
        return reservaService.cancelarReserva(id);
    }

    // 5. Consultar disponibilidad de un espacio
    @GetMapping("/disponibilidad")
    public DisponibilidadDTO consultarDisponibilidad(
            @RequestParam Integer idEspacio,
            @RequestParam LocalDate fecha,
            @RequestParam String horaInicio,
            @RequestParam String horaFin) {

        return reservaService.consultarDisponibilidad(
                idEspacio,
                fecha,
                horaInicio,
                horaFin
        );
    }
}

