package com.plassb.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Notificación recibida de Ecoembes: contenedores y envases que llegarán a la planta un día
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private int numContenedores;
    private int totalEnvases;
    private double toneladas;
    private LocalDateTime fechaHoraRecepcion;

    protected Notificacion() { } //Constructor vacío que necesita JPA

    public Notificacion(LocalDate fecha, int numContenedores, int totalEnvases, double toneladas) {
        this.fecha = fecha;
        this.numContenedores = numContenedores;
        this.totalEnvases = totalEnvases;
        this.toneladas = toneladas;
        this.fechaHoraRecepcion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public int getNumContenedores() { return numContenedores; }
    public int getTotalEnvases() { return totalEnvases; }
    public double getToneladas() { return toneladas; }
    public LocalDateTime getFechaHoraRecepcion() { return fechaHoraRecepcion; }
}
