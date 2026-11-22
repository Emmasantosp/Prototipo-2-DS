package com.contsocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;

/**
 * Atiende una conexión: lee un mensaje, lo procesa y envía la respuesta.
 * Protocolo (texto con writeUTF/readUTF, campos separados por ';'):
 *   CONSULTAR_CAPACIDAD;fecha                                  -> OK;fecha;capacidadToneladas
 *                                                                 | ERROR;400;Fecha inválida | ERROR;404;No hay capacidad publicada para esa fecha
 *   NOTIFICAR_ASIGNACION;fecha;numContenedores;totalEnvases    -> OK;idNotificacion | ERROR;400;Datos inválidos
 *   Cualquier otro mensaje                                     -> ERROR;400;Comando desconocido
 */
public class ClienteHandler implements Runnable {

    private final Socket socket;
    private final CapacidadStore store;

    public ClienteHandler(Socket socket, CapacidadStore store) {
        this.socket = socket;
        this.store = store;
    }

    @Override
    public void run() {
        try (Socket s = socket;
             DataInputStream in = new DataInputStream(s.getInputStream());
             DataOutputStream out = new DataOutputStream(s.getOutputStream())) {
            String mensaje = in.readUTF();
            String respuesta = procesar(mensaje);
            System.out.println("[ContSocket] " + mensaje + " -> " + respuesta);
            out.writeUTF(respuesta);
            out.flush();
        } catch (IOException e) {
            System.err.println("[ContSocket] Error en la conexión: " + e.getMessage());
        }
    }

    String procesar(String mensaje) {
        String[] partes = mensaje.split(";");
        for (int i = 0; i < partes.length; i++) {
            partes[i] = partes[i].trim();
        }
        switch (partes[0]) {
            case "CONSULTAR_CAPACIDAD":
                return consultarCapacidad(partes);
            case "NOTIFICAR_ASIGNACION":
                return notificarAsignacion(partes);
            default:
                return "ERROR;400;Comando desconocido";
        }
    }

    private String consultarCapacidad(String[] partes) {
        if (partes.length != 2) {
            return "ERROR;400;Fecha inválida";
        }
        try {
            LocalDate fecha = LocalDate.parse(partes[1]);
            Optional<Double> capacidad = store.consultarCapacidad(fecha);
            return capacidad.map(t -> String.format(Locale.ROOT, "OK;%s;%.3f", fecha, t))
                    .orElse("ERROR;404;No hay capacidad publicada para esa fecha");
        } catch (DateTimeParseException e) {
            return "ERROR;400;Fecha inválida";
        }
    }

    private String notificarAsignacion(String[] partes) {
        if (partes.length != 4) {
            return "ERROR;400;Datos inválidos";
        }
        try {
            LocalDate fecha = LocalDate.parse(partes[1]);
            int numContenedores = Integer.parseInt(partes[2]);
            int totalEnvases = Integer.parseInt(partes[3]);
            if (numContenedores <= 0 || totalEnvases < 0) {
                return "ERROR;400;Datos inválidos";
            }
            int id = store.registrarNotificacion(fecha, numContenedores, totalEnvases);
            return "OK;" + id;
        } catch (DateTimeParseException | NumberFormatException e) {
            return "ERROR;400;Datos inválidos";
        }
    }
}
