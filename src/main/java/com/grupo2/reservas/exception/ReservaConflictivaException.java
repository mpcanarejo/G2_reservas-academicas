package com.grupo2.reservas.exception;

public class ReservaConflictivaException extends RuntimeException {

    public ReservaConflictivaException(String mensaje) {
        super(mensaje);
    }
}