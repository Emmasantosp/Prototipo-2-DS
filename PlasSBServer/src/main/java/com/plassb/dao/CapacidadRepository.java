package com.plassb.dao;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plassb.entity.CapacidadDiaria;

/** DAO de la capacidad diaria de la planta. */
@Repository
public interface CapacidadRepository extends JpaRepository<CapacidadDiaria, LocalDate> {
}
