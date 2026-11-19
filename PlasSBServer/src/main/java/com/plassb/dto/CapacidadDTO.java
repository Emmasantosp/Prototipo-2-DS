package com.plassb.dto;

import java.time.LocalDate;

//Capacidad disponible de la planta para un día, en toneladas
public record CapacidadDTO(LocalDate fecha, double toneladas) {
}
