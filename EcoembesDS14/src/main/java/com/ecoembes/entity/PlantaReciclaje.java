package com.ecoembes.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Planta de reciclaje externa (PlasSB Ltd. y ContSocket Ltd.)
//Desde el Prototipo 2 la capacidad no se guarda aquí: se pide a la planta a través de su Service Gateway.
//tipoServicio indica a la factoría qué gateway hay que usar para hablar con la planta.
@Entity
@Table(name = "plantas")
public class PlantaReciclaje {

    @Id
    private String id;
    private String nombre;
    private String tipoServicio; //"PlasSB" (REST) o "ContSocket" (sockets)

    protected PlantaReciclaje() { } //Constructor vacío que necesita JPA

    public PlantaReciclaje(String id, String nombre, String tipoServicio) {
        this.id = id;
        this.nombre = nombre;
        this.tipoServicio = tipoServicio;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipoServicio() { return tipoServicio; }
}
