package com.grupo2.reservas.entity;

import java.time.LocalDate;


import jakarta.persistence.*;

@Entity
@Table(name = "reserva")
public class Reserva {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id_reserva")
    private Long idReserva;

    ///    
    @ManyToOne 
    @JoinColumn (name = "ID_USUARIO",nullable = false)
    private Usuario usuario;
    @ManyToOne 
    @JoinColumn (name = "ID_ESPACIO",nullable = false)
    private Espacio espacio;

    @Column(name = "FECHA",nullable = false)
    private LocalDate fechaReserva;
    @Column (name = "ESTADO",nullable = false)
    private String estado;
    @Column (name = "HORA_INICIO",nullable = false)
    private String hora_inicio;
    @Column (name = "HORA_FIN",nullable = false )
    private String hora_fin;      
 

    public Long getIdReserva() {
        return idReserva;
    }
    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Espacio getEspacio() {
        return espacio;
    }
    public void setEspacio(Espacio espacio) {
        this.espacio = espacio;
    }
    public LocalDate getFechaReserva() {
        return fechaReserva;
    }
    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getHoraInicio() {
        return hora_inicio;
    }
    public void setHoraInicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }
    public String getHoraFin() {
        return hora_fin;
    }
    public void setHoraFin(String hora_fin) {
        this.hora_fin = hora_fin;
    }
   

    
}
