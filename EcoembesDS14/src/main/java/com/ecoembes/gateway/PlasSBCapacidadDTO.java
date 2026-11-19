package com.ecoembes.gateway;

import java.time.LocalDate;

//Respuesta de GET /api/capacidad del servidor PlasSB
public record PlasSBCapacidadDTO(LocalDate fecha, double toneladas) {
}
