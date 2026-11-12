package com.grupo2.reservas.exception;

// Excepcion personalizada pro recurso no encontrado
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}