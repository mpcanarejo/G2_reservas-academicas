package com.grupo2.reservas.exception;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> recursoNoEncontrado(RecursoNoEncontradoException ex) {
        Map<String, String> cuerpo = new HashMap<>();
        cuerpo.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo);
    }

    @ExceptionHandler(ReservaConflictivaException.class)
    public ResponseEntity<Map<String, String>> reservaConflictiva(ReservaConflictivaException ex) {
        Map<String, String> cuerpo = new HashMap<>();
        cuerpo.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo);
    }

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos(DatosInvalidosException ex) {
        Map<String, String> cuerpo = new HashMap<>();
        cuerpo.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> detalles = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            detalles.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", "Datos inválidos en la petición");
        cuerpo.put("detalles", detalles);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> peticionInvalida(Exception ex) {
        Map<String, String> cuerpo = new HashMap<>();
        cuerpo.put("mensaje", "La petición no tiene un formato válido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> errorGeneral(Exception ex) {
        log.error("Error no controlado", ex);
        Map<String, String> cuerpo = new HashMap<>();
        cuerpo.put("mensaje", "Error interno del servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cuerpo);
    }
}
