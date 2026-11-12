package com.grupo2.reservas.exception;

// Esta excepción es personalizada para verificar por ejemplo datos inválidos

public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}