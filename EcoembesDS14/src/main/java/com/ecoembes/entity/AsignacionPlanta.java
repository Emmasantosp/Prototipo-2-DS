package com.ecoembes.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//Asignación de uno o varios contenedores a una planta para un día (con auditoría)
@Entity
@Table(name = "asignaciones")
public class AsignacionPlanta {

    @Id
    private String id;
    private LocalDate fecha; //Día en que la planta recibirá los envases

    @ManyToOne(optional = false)
    @JoinColumn(name = "planta_id")
    private PlantaReciclaje planta;

    @ManyToMany
    @JoinTable(name = "asignacion_contenedores",
               joinColumns = @JoinColumn(name = "asignacion_id"),
               inverseJoinColumns = @JoinColumn(name = "contenedor_id"))
    private List<Contenedor> contenedores;

    private int totalEnvases;
    private double capacidadRestanteToneladas; //Capacidad que le quedaba a la planta tras la asignación

    @ManyToOne(optional = false)
    @JoinColumn(name = "empleado_id")
    private Empleado asignadoPor; //Auditoría: quién hizo la asignación
    private LocalDateTime fechaHoraRegistro; //Auditoría: cuándo se hizo

    protected AsignacionPlanta() { } //Constructor vacío que necesita JPA

    public AsignacionPlanta(String id, LocalDate fecha, PlantaReciclaje planta,
                            List<Contenedor> contenedores, Empleado asignadoPor,
                            double capacidadRestanteToneladas) {
        this.id = id;
        this.fecha = fecha;
        this.planta = planta;
        this.contenedores = contenedores;
        this.asignadoPor = asignadoPor;
        this.totalEnvases = contenedores.stream().mapToInt(Contenedor::getNumEnvases).sum();
        this.capacidadRestanteToneladas = capacidadRestanteToneladas;
        this.fechaHoraRegistro = LocalDateTime.now();
    }

    public String getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public PlantaReciclaje getPlanta() { return planta; }
    public List<Contenedor> getContenedores() { return contenedores; }
    public int getTotalEnvases() { return totalEnvases; }
    public double getCapacidadRestanteToneladas() { return capacidadRestanteToneladas; }
    public Empleado getAsignadoPor() { return asignadoPor; }
    public LocalDateTime getFechaHoraRegistro() { return fechaHoraRegistro; }
}
