package com.ecoembes.gateway;

import java.time.LocalDate;

//Respuesta de GET /plassb/capacidad del servidor PlasSB
public record PlasSBCapacidadDTO(LocalDate fecha, double capacidadToneladas) {
}
