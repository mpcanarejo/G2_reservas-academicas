package com.grupo2.reservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Aqui comienza la aplicacion es decir arranca el programa
@SpringBootApplication
// Este es el metod principal donde el Spring arranca el servidor web
public class ReservasApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReservasApplication.class, args);
    }
}