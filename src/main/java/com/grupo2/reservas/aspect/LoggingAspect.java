package com.grupo2.reservas.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.grupo2.reservas.service..*(..))")
    public Object medirTiempo(ProceedingJoinPoint puntoDeUnion) throws Throwable {
        String metodo = puntoDeUnion.getSignature().toString();
        System.out.println("[Aspecto] Ejecutando: " + metodo);
        long inicio = System.currentTimeMillis();
        try {
            Object resultado = puntoDeUnion.proceed();
            long fin = System.currentTimeMillis();
            System.out.println("[Aspecto] " + metodo + " tardó " + (fin - inicio) + " ms");
            return resultado;
        } catch (Throwable error) {
            System.out.println("[Aspecto] " + metodo + " falló: " + error.getMessage());
            throw error;
        }
    }
}
