package com.ecoembes.gateway;

import java.time.LocalDate;

//Body de POST /plassb/notificaciones del servidor PlasSB
public record PlasSBNotificacionDTO(LocalDate fecha, int numContenedores, int totalEnvases) {
}
