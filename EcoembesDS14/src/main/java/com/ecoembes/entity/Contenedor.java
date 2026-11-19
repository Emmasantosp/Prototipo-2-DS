package com.ecoembes.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

//Contenedor de reciclaje equipado con el sensor RecycloBin UltraEye
@Entity
@Table(name = "contenedores")
public class Contenedor {

    @Id
    private String id;
    private String ubicacion; //Dirección
    private String codigoPostal; //Identifica la zona
    private int capacidad; //Capacidad inicial (nº de envases)
    private int numEnvases; //Última lectura del sensor
    @Enumerated(EnumType.STRING)
    private NivelLlenado nivelLlenado;

    //Histórico de lecturas. Se guarda en cascada con el contenedor.
    //EAGER porque las consultas de uso y de zona recorren siempre el historial
    @OneToMany(mappedBy = "contenedor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("fecha ASC")
    private List<RegistroUsoContenedor> historial = new ArrayList<>();

    protected Contenedor() { } //Constructor vacío que necesita JPA

    public Contenedor(String id, String ubicacion, String codigoPostal, int capacidad) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.codigoPostal = codigoPostal;
        this.capacidad = capacidad;
        this.numEnvases = 0;
        this.nivelLlenado = NivelLlenado.VERDE;
    }

    //Registra una lectura del sensor y actualiza el estado actual del contenedor
    public void registrarLectura(LocalDate fecha, int numEnvases, NivelLlenado nivel) {
        historial.removeIf(r -> r.getFecha().equals(fecha)); //Una lectura por día
        historial.add(new RegistroUsoContenedor(this, fecha, numEnvases, nivel));
        RegistroUsoContenedor ultimo = historial.stream()
                .max(Comparator.comparing(RegistroUsoContenedor::getFecha))
                .orElseThrow();
        this.numEnvases = ultimo.getNumEnvases();
        this.nivelLlenado = ultimo.getNivelLlenado();
    }

    //Lecturas comprendidas entre una fecha de inicio y una de fin (ambas incluidas)
    public List<RegistroUsoContenedor> getHistorial(LocalDate inicio, LocalDate fin) {
        return historial.stream()
                .filter(r -> !r.getFecha().isBefore(inicio) && !r.getFecha().isAfter(fin))
                .sorted(Comparator.comparing(RegistroUsoContenedor::getFecha))
                .toList();
    }

    //Última lectura disponible en una fecha (la de ese día o la anterior más reciente)
    public Optional<RegistroUsoContenedor> getEstadoEnFecha(LocalDate fecha) {
        return historial.stream()
                .filter(r -> !r.getFecha().isAfter(fecha))
                .max(Comparator.comparing(RegistroUsoContenedor::getFecha));
    }

    public String getId() { return id; }
    public String getUbicacion() { return ubicacion; }
    public String getCodigoPostal() { return codigoPostal; }
    public int getCapacidad() { return capacidad; }
    public int getNumEnvases() { return numEnvases; }
    public NivelLlenado getNivelLlenado() { return nivelLlenado; }
}
