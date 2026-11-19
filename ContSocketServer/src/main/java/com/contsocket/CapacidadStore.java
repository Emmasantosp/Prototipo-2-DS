package com.contsocket;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Datos de la planta en memoria: capacidad disponible por día y notificaciones recibidas. */
public class CapacidadStore {

    private final Map<LocalDate, Double> disponible = new HashMap<>();
    private final List<String> notificaciones = new ArrayList<>();

    //Capacidad de hoy y de los 10 días siguientes. Hoy + 2 casi sin capacidad para probar el rechazo.
    public synchronized void cargarDatosIniciales() {
        LocalDate hoy = LocalDate.now();
        for (int d = 0; d <= 10; d++) {
            disponible.put(hoy.plusDays(d), d == 2 ? 0.01 : 15.0 + Math.floorMod(d, 4) * 3.0);
        }
    }

    public synchronized Optional<Double> consultar(LocalDate fecha) {
        return Optional.ofNullable(disponible.get(fecha));
    }

    //Descuenta las toneladas y guarda la notificación. Devuelve la respuesta del protocolo
    public synchronized String registrarNotificacion(LocalDate fecha, int contenedores, int envases, double toneladas) {
        Double actual = disponible.get(fecha);
        if (actual == null) {
            return "ERROR;Sin capacidad publicada para " + fecha;
        }
        if (toneladas > actual) {
            return String.format(Locale.ROOT, "ERROR;Capacidad insuficiente para %s: quedan %.3f t", fecha, actual);
        }
        disponible.put(fecha, actual - toneladas);
        notificaciones.add(String.format(Locale.ROOT, "%s: %d contenedores, %d envases (%.3f t)", fecha, contenedores, envases, toneladas));
        return "OK";
    }

    public synchronized List<String> getNotificaciones() {
        return List.copyOf(notificaciones);
    }
}
