package com.grupo2.reservas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupo2.reservas.dto.HorarioDTO;
import com.grupo2.reservas.service.HorarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    public List<HorarioDTO> listar(@RequestParam(required = false) Long espacioId) {
        if (espacioId != null) {
            return horarioService.obtenerPorEspacio(espacioId);
        }
        return horarioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public HorarioDTO obtenerPorId(@PathVariable Long id) {
        return horarioService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<HorarioDTO> crear(@Valid @RequestBody HorarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioService.guardar(dto));
    }

    @PutMapping("/{id}")
    public HorarioDTO actualizar(@PathVariable Long id, @Valid @RequestBody HorarioDTO dto) {
        return horarioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        horarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
