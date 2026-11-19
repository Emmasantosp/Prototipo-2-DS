package com.plassb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.plassb.entity.Notificacion;

/** DAO de las notificaciones recibidas de Ecoembes. */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findAllByOrderByFechaHoraRecepcionDesc();
}
