package com.ecoembes.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecoembes.entity.Empleado;

/** DAO de empleados. Spring Data JPA genera la implementación a partir de la interfaz. */
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, String> {

    Optional<Empleado> findByEmail(String email);
}
