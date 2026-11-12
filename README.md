# Taller Grupal Reservas Espacios Académicos

Desarrollo de una API para la gestión de espacios académicos:
Salones, Laboratorios y salas de reuniones

## Integrantes Grupo 2

| Integrante | Parte asignada |
|------------|----------------|
| Sofía Rivas | `Espacio`: Entity, Repository, Service, Controller y DTO |
| José Angulo | `Usuario` y `Horario`: Entity, Repository, Service, Controller y DTO |
| Jessica Sanchez | `Reserva` y `DisponibilidadDTO`: Entity, Repository, Service, Controller y DTO |
| Marco Cañarejo| Excepciones y manejo centralizado, configuración (WebConfig), interceptor HTTP, aspecto (AOP), `application.properties`, scripts de base de datos, CI, `pom.xml` y `README.md` |

## Tecnologías

- **Java 27** y **Spring Boot 4.1**
- **Spring Web MVC**
- **Spring Data JPA**
- **Base de datos relacional**

## Arquitectura de capas

```
Petición HTTP
     |
     ▼
LoggingInterceptor: Se activa y se intercepta los registro de todas las solicitudes
     |
     ▼
Controller  --►  Service  --►  Repository  --►  Entity / Base de datos
 (recibe y        (lógica  -->      (consultas       (mapeo de tablas)
  valida el        de negocio)    Spring Data)
  DTO)
     │
     ├-- si hay algún error --► GlobalExceptionHandler --► respuesta HTTP (404/409/400)
     │
     ▼
LoggingAspect: Que nos permite medir el tiempo de ejecución de cada controller
```

- En las condiciones del proyecto los controllers no contienen lógica de negocio
- Todas las respuestas que se generan con códigos HTTP y el mensaje respectivo


## Estructura del proyecto

```
reservas-academicas/
├-- src/main/java/com/grupo2/reservas/
│   ├-- ReservasApplication.java         
│   ├-- controller/                      
│   ├-- service/                          
│   ├-- repository/                       
│   ├-- entity/                          
│   ├-- dto/                              
│   ├-- exception/                        
│   ├-- config/WebConfig.java             
│   ├-- interceptor/LoggingInterceptor.java  
│   └-- aspect/LoggingAspect.java        
├-- src/main/resources/application.properties
├-- src/test/java/                        
├-- database/
│   ├-- schema.sql                       
│   └-- datos_prueba.sql                  
└-- .github/workflows/ci.yml  