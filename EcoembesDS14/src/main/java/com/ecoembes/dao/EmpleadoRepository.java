package com.ecoembes.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoembes.entity.Empleado;

/** DAO de empleados. Spring Data JPA genera la implementación a partir de la interfaz. */
public interface EmpleadoRepository extends JpaRepository<Empleado, String> {

    Optional<Empleado> findByEmail(String email);
}
