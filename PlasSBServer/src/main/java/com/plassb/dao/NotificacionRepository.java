package com.plassb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plassb.entity.Notificacion;

/** DAO de las notificaciones recibidas de Ecoembes. */
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findAllByOrderByFechaHoraRecepcionDesc();
}
