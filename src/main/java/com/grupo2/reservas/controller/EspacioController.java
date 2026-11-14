package com.grupo2.reservas.controller;

import com.grupo2.reservas.dto.EspacioDTO;
import com.grupo2.reservas.service.EspacioService;
import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("/api/espacios")
public class EspacioController {

    private final EspacioService espacioService;

    public EspacioController(EspacioService espacioService) {
        this.espacioService = espacioService;
    }

    @GetMapping
    public ResponseEntity<List<EspacioDTO>> listar() {
        return ResponseEntity.ok(espacioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspacioDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(espacioService.obtenerPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<EspacioDTO>> porTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(espacioService.listarPorTipo(tipo));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EspacioDTO>> activos() {
        return ResponseEntity.ok(espacioService.listarActivos());
    }

    @GetMapping("/capacidad")
    public ResponseEntity<List<EspacioDTO>> porCapacidad(@RequestParam Integer minima) {
        return ResponseEntity.ok(espacioService.listarPorCapacidadMinima(minima));
    }

    @PostMapping
    public ResponseEntity<EspacioDTO> crear(@Valid @RequestBody EspacioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(espacioService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspacioDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EspacioDTO dto) {
        return ResponseEntity.ok(espacioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        espacioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}