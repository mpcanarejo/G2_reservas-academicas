package com.grupo2.reservas.dto;

import java.time.LocalDate;


public class ReservaDTO {

    private Integer idReserva;
    private Integer idUsuario;
    private Integer idEspacio;
    private LocalDate fechaReserva;
    private String hora_inicio;
    private String hora_fin;
    private String estado;

    //constructor
    public ReservaDTO(){

    }
    //constructor con parmetso en la base de datos
    public ReservaDTO(Integer idReserva, Integer idUsuario, Integer idHorario,
        Integer idEspacio, LocalDate fechaReserva, String hora_inicio, String hora_fin,
        String estado){

        
        //
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idEspacio = idEspacio; 
        this.fechaReserva = fechaReserva;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;   
        this.estado = estado;
        }

        //get y setters

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(int idEspacio) {
        this.idEspacio = idEspacio;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
    
}

