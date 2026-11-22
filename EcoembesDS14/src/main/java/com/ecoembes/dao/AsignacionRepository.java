package com.ecoembes.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoembes.entity.AsignacionPlanta;

/** DAO de asignaciones (incluye la auditoría: quién y cuándo). */
public interface AsignacionRepository extends JpaRepository<AsignacionPlanta, String> {

    //Asignaciones ya hechas a una planta para un día (para descontarlas de su capacidad)
    List<AsignacionPlanta> findByPlantaIdAndFecha(String idPlanta, LocalDate fecha);
}
