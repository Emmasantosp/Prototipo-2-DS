package com.contsocket;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Persistencia en memoria de la planta: un mapa con la capacidad de cada día
 * y una lista con las notificaciones recibidas. Los datos se pierden al apagar el servicio.
 */
public class CapacidadStore {

    private final Map<LocalDate, Double> capacidades = new HashMap<>();
    private final List<String> notificaciones = new ArrayList<>();

    //Capacidad de hoy y de los 10 días siguientes. Hoy + 2 casi sin capacidad para probar el rechazo por capacidad insuficiente.
    public synchronized void cargarDatosIniciales() {
        LocalDate hoy = LocalDate.now();
        for (int d = 0; d <= 10; d++) {
            capacidades.put(hoy.plusDays(d), d == 2 ? 0.01 : 15.0 + Math.floorMod(d, 4) * 3.0);
        }
    }

    public synchronized Optional<Double> consultarCapacidad(LocalDate fecha) {
        return Optional.ofNullable(capacidades.get(fecha));
    }

    //Guarda la notificación y devuelve su id (posición en la lista, empezando en 1)
    public synchronized int registrarNotificacion(LocalDate fecha, int numContenedores, int totalEnvases) {
        notificaciones.add(String.format(Locale.ROOT, "%s: %d contenedores, %d envases", fecha, numContenedores, totalEnvases));
        return notificaciones.size();
    }
}
