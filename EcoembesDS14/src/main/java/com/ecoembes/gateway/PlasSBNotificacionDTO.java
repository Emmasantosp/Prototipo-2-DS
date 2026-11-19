package com.ecoembes.gateway;

import java.time.LocalDate;

//Body de POST /api/notificaciones del servidor PlasSB
public record PlasSBNotificacionDTO(LocalDate fecha, int numContenedores, int totalEnvases, double toneladas) {
}
