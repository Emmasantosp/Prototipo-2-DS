package com.plassb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Servidor de la planta de reciclaje PlasSB Ltd. (servicio externo del Prototipo 2).
 * Spring Boot con persistencia JPA + H2. Publica su capacidad diaria y recibe las notificaciones de Ecoembes.
 */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "API REST de PlasSB Ltd.",
        version = "Prototipo 2",
        description = "Capacidad diaria de la planta y notificaciones de asignación enviadas por el Servidor Ecoembes."))
public class PlasSBApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlasSBApplication.class, args);
    }
}
