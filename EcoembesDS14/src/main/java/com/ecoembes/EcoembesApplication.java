package com.ecoembes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Servidor Ecoembes - Prototipo 2 (grupo DS-14).
 * Persistencia con JPA + H2 (patrón DAO) y comunicación con las plantas PlasSB y ContSocket
 * mediante Service Gateways creados por una factoría.
 */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "API REST del Servidor Ecoembes",
        version = "Prototipo 2",
        description = "Gestión de contenedores, plantas de reciclaje y asignaciones. Grupo DS-14."))
public class EcoembesApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcoembesApplication.class, args);
    }
}
