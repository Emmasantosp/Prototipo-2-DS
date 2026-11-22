package com.ecoembes.entity;

import java.time.LocalDate;

//Capacidad de reciclaje disponible (en toneladas) de una planta para un día concreto.
//No se persiste en Ecoembes: se calcula con lo que devuelve el Service Gateway de la planta
//menos lo que Ecoembes ya le ha asignado ese día.
public class CapacidadPlanta {

    private final PlantaReciclaje planta;
    private final LocalDate fecha;
    private final double toneladas;

    public CapacidadPlanta(PlantaReciclaje planta, LocalDate fecha, double toneladas) {
        this.planta = planta;
        this.fecha = fecha;
        this.toneladas = toneladas;
    }

    public PlantaReciclaje getPlanta() { return planta; }
    public LocalDate getFecha() { return fecha; }
    public double getToneladas() { return toneladas; }
}
