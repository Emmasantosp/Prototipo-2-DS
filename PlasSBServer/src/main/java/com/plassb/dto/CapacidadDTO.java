package com.plassb.dto;

import java.time.LocalDate;

//Capacidad de reciclaje de la planta para un día, en toneladas
public record CapacidadDTO(LocalDate fecha, double capacidadToneladas) {
}
