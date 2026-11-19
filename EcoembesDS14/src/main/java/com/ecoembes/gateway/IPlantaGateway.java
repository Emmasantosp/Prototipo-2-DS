package com.ecoembes.gateway;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Patrón Service Gateway: interfaz común para hablar con cualquier planta de reciclaje.
 * Los AppServices solo conocen esta interfaz, nunca la implementación concreta (REST o sockets).
 */
public interface IPlantaGateway {

    /** Capacidad disponible (toneladas) para un día. Vacío si la planta no ha publicado capacidad ese día. */
    Optional<Double> consultarCapacidad(LocalDate fecha);

    /** Notifica a la planta los contenedores y envases que recibirá ese día. */
    void notificarAsignacion(LocalDate fecha, int numContenedores, int totalEnvases, double toneladas);
}
