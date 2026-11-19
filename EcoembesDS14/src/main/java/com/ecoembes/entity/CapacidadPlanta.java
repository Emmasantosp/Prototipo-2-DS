package com.ecoembes.entity;

import java.time.LocalDate;

//Capacidad de reciclaje (en toneladas) que una planta ofrece para un día concreto.
//Desde el Prototipo 2 no se persiste en Ecoembes: es el valor que devuelve el Service Gateway de la planta.
public class CapacidadPlanta {

    private final LocalDate fecha;
    private final double toneladas;

    public CapacidadPlanta(LocalDate fecha, double toneladas) {
        this.fecha = fecha;
        this.toneladas = toneladas;
    }

    public LocalDate getFecha() { return fecha; }
    public double getToneladas() { return toneladas; }
}
