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
    private double capacidadTotal; //Toneladas que la planta puede procesar ese día
    private double capacidadOcupada; //Toneladas ya comprometidas por asignaciones de Ecoembes

    protected CapacidadDiaria() { } //Constructor vacío que necesita JPA

    public CapacidadDiaria(LocalDate fecha, double capacidadTotal) {
        this.fecha = fecha;
        this.capacidadTotal = capacidadTotal;
        this.capacidadOcupada = 0;
    }

    public double getDisponible() {
        return Math.round((capacidadTotal - capacidadOcupada) * 1000) / 1000.0;
    }

    public void ocupar(double toneladas) {
        this.capacidadOcupada += toneladas;
    }

    public LocalDate getFecha() { return fecha; }
    public double getCapacidadTotal() { return capacidadTotal; }
    public double getCapacidadOcupada() { return capacidadOcupada; }
}
