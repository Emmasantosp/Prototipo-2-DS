package com.ecoembes.dto;

import java.time.LocalDate;

//Capacidad disponible de una planta para un día, en toneladas
public record CapacidadPlantaDTO(String idPlanta, String nombrePlanta, LocalDate fecha,
                                 double capacidadDisponibleToneladas) {
}
