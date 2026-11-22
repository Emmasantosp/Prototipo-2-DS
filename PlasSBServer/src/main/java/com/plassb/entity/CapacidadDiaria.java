package com.plassb.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Capacidad de reciclaje (toneladas) que PlasSB publica para un día
@Entity
@Table(name = "capacidades")
public class CapacidadDiaria {

    @Id
    private LocalDate fecha;
    private double capacidadToneladas; //Toneladas que la planta puede procesar ese día

    protected CapacidadDiaria() { } //Constructor vacío que necesita JPA

    public CapacidadDiaria(LocalDate fecha, double capacidadToneladas) {
        this.fecha = fecha;
        this.capacidadToneladas = capacidadToneladas;
    }

    public LocalDate getFecha() { return fecha; }
    public double getCapacidadToneladas() { return capacidadToneladas; }
}
