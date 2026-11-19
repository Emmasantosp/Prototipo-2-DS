package com.ecoembes.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//Lectura diaria enviada por el sensor de un contenedor (histórico de uso)
@Entity
@Table(name = "registros_uso")
public class RegistroUsoContenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contenedor_id")
    private Contenedor contenedor; //Contenedor al que pertenece la lectura

    private LocalDate fecha;
    private int numEnvases;
    @Enumerated(EnumType.STRING)
    private NivelLlenado nivelLlenado;

    protected RegistroUsoContenedor() { } //Constructor vacío que necesita JPA

    public RegistroUsoContenedor(Contenedor contenedor, LocalDate fecha, int numEnvases, NivelLlenado nivelLlenado) {
        this.contenedor = contenedor;
        this.fecha = fecha;
        this.numEnvases = numEnvases;
        this.nivelLlenado = nivelLlenado;
    }

    public Long getId() { return id; }
    public Contenedor getContenedor() { return contenedor; }
    public LocalDate getFecha() { return fecha; }
    public int getNumEnvases() { return numEnvases; }
    public NivelLlenado getNivelLlenado() { return nivelLlenado; }
}
