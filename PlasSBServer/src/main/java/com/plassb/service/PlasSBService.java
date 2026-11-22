package com.plassb.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plassb.dao.CapacidadRepository;
import com.plassb.dao.NotificacionRepository;
import com.plassb.entity.CapacidadDiaria;
import com.plassb.entity.Notificacion;

/** AppService de PlasSB: consulta de capacidad y registro de notificaciones. */
@Service
public class PlasSBService {

    private final CapacidadRepository capacidadRepository;
    private final NotificacionRepository notificacionRepository;

    public PlasSBService(CapacidadRepository capacidadRepository, NotificacionRepository notificacionRepository) {
        this.capacidadRepository = capacidadRepository;
        this.notificacionRepository = notificacionRepository;
    }

    //Capacidad publicada para un día. Vacío si la planta no la ha publicado
    @Transactional(readOnly = true)
    public Optional<CapacidadDiaria> consultarCapacidad(LocalDate fecha) {
        return capacidadRepository.findById(fecha);
    }

    //Guarda en la BD la notificación enviada por Ecoembes
    @Transactional
    public Notificacion registrarNotificacion(LocalDate fecha, int numContenedores, int totalEnvases) {
        System.out.printf("[PlasSB] Notificación recibida para %s: %d contenedores, %d envases%n",
                fecha, numContenedores, totalEnvases);
        return notificacionRepository.save(new Notificacion(fecha, numContenedores, totalEnvases));
    }

    @Transactional(readOnly = true)
    public List<Notificacion> getNotificaciones() {
        return notificacionRepository.findAllByOrderByFechaHoraRecepcionDesc();
    }
}
