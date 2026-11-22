package com.ecoembes.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoembes.entity.Contenedor;

/** DAO de contenedores (el historial de lecturas se guarda en cascada con cada contenedor). */
public interface ContenedorRepository extends JpaRepository<Contenedor, String> {

    //Contenedores de una zona (código postal) ordenados por id
    List<Contenedor> findByCodigoPostalOrderByIdAsc(String codigoPostal);
}
