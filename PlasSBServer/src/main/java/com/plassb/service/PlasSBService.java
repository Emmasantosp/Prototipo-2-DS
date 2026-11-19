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

    //Registra la notificación y descuenta las toneladas de la capacidad de ese día
    @Transactional
    public synchronized Notificacion registrarNotificacion(LocalDate fecha, int numContenedores, int totalEnvases, double toneladas) {
        CapacidadDiaria capacidad = capacidadRepository.findById(fecha)
                .orElseThrow(() -> new IllegalStateException("PlasSB no ha publicado capacidad para " + fecha));
        if (toneladas > capacidad.getDisponible()) {
            throw new IllegalStateException("Capacidad insuficiente para " + fecha + ": quedan " + capacidad.getDisponible() + " t");
        }
        capacidad.ocupar(toneladas);
        capacidadRepository.save(capacidad);
        System.out.printf("[PlasSB] Notificación recibida para %s: %d contenedores, %d envases (%.3f t)%n",
                fecha, numContenedores, totalEnvases, toneladas);
        return notificacionRepository.save(new Notificacion(fecha, numContenedores, totalEnvases, toneladas));
    }

    @Transactional(readOnly = true)
    public List<Notificacion> getNotificaciones() {
        return notificacionRepository.findAllByOrderByFechaHoraRecepcionDesc();
    }
}
