package com.grupo2.reservas.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "HORARIO")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación: MUCHOS horarios pertenecen a UN espacio
    @ManyToOne
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Horario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Espacio getEspacio() {
        return espacio;
    }

    public void setEspacio(Espacio espacio) {
        this.espacio = espacio;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}