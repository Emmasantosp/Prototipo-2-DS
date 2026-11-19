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
 * Atiende una conexión: lee una petición, la procesa y envía la respuesta.
 * Protocolo (texto con writeUTF/readUTF):
 *   CAPACIDAD;AAAA-MM-DD                                   -> OK;toneladas | SIN_CAPACIDAD
 *   NOTIFICAR;AAAA-MM-DD;contenedores;envases;toneladas    -> OK | ERROR;mensaje
 *   Cualquier otra cosa                                    -> ERROR;mensaje
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
            String peticion = in.readUTF();
            String respuesta = procesar(peticion);
            System.out.println("[ContSocket] " + peticion + " -> " + respuesta);
            out.writeUTF(respuesta);
            out.flush();
        } catch (IOException e) {
            System.err.println("[ContSocket] Error en la conexión: " + e.getMessage());
        }
    }

    String procesar(String peticion) {
        String[] partes = peticion.split(";");
        try {
            switch (partes[0]) {
                case "CAPACIDAD": {
                    LocalDate fecha = LocalDate.parse(partes[1]);
                    Optional<Double> disponible = store.consultar(fecha);
                    return disponible.map(t -> String.format(Locale.ROOT, "OK;%.3f", t)).orElse("SIN_CAPACIDAD");
                }
                case "NOTIFICAR": {
                    LocalDate fecha = LocalDate.parse(partes[1]);
                    int contenedores = Integer.parseInt(partes[2]);
                    int envases = Integer.parseInt(partes[3]);
                    double toneladas = Double.parseDouble(partes[4]);
                    return store.registrarNotificacion(fecha, contenedores, envases, toneladas);
                }
                default:
                    return "ERROR;Operación desconocida: " + partes[0];
            }
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException | NumberFormatException e) {
            return "ERROR;Petición mal formada: " + peticion;
        }
    }
}
