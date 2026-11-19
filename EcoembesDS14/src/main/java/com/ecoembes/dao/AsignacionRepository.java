package com.ecoembes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecoembes.entity.AsignacionPlanta;

/** DAO de asignaciones (incluye la auditoría: quién y cuándo). */
@Repository
public interface AsignacionRepository extends JpaRepository<AsignacionPlanta, String> {
}
