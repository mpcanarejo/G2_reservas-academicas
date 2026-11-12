package com.grupo2.reservas.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String INICIO = "inicioPeticion";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(INICIO, System.currentTimeMillis());
        System.out.println("==================================================");
        System.out.println("[Interceptor] Petición recibida");
        System.out.println("[Interceptor] Método HTTP : " + request.getMethod());
        System.out.println("[Interceptor] Ruta        : " + request.getRequestURI());
        System.out.println("[Interceptor] Cliente     : " + request.getRemoteAddr());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object inicio = request.getAttribute(INICIO);
        long tiempo = inicio instanceof Long valor ? System.currentTimeMillis() - valor : 0;
        System.out.println("[Interceptor] Petición finalizada");
        System.out.println("[Interceptor] Estado HTTP : " + response.getStatus());
        System.out.println("[Interceptor] Tiempo      : " + tiempo + " ms");
        System.out.println("==================================================");
    }
}
