package com.grupo2.reservas.controller;

import com.grupo2.reservas.entity.Horario;
import com.grupo2.reservas.service.HorarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    public List<Horario> listar(@RequestParam(required = false) Long espacioId) {
        if (espacioId != null) {
            return horarioService.obtenerPorEspacio(espacioId);
        }
        return horarioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Horario obtenerPorId(@PathVariable Long id) {
        return horarioService.obtenerPorId(id);
    }

    @PostMapping
    public Horario crear(@RequestBody Horario horario) {
        return horarioService.guardar(horario);
    }

    @PutMapping("/{id}")
    public Horario actualizar(@PathVariable Long id, @RequestBody Horario horario) {
        return horarioService.actualizar(id, horario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        horarioService.eliminar(id);
    }
}