package com.ecoembes.gateway;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Optional;

import com.ecoembes.exception.PeticionInvalidaException;
import com.ecoembes.exception.ServicioExternoException;

/**
 * Service Gateway de ContSocket Ltd.: se comunica con su servidor mediante sockets TCP.
 * Protocolo de texto (writeUTF/readUTF), una petición por conexión:
 *   CONSULTAR_CAPACIDAD;AAAA-MM-DD                                   -> OK;fecha;capacidadToneladas | ERROR;404;mensaje | ERROR;400;mensaje
 *   NOTIFICAR_ASIGNACION;AAAA-MM-DD;numContenedores;totalEnvases     -> OK;idNotificacion | ERROR;400;mensaje
 */
public class ContSocketGateway implements IPlantaGateway {

    private static final int TIMEOUT_MS = 5000;

    private final String host;
    private final int puerto;

    public ContSocketGateway(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    @Override
    public Optional<Double> consultarCapacidad(LocalDate fecha) {
        String[] respuesta = enviar("CONSULTAR_CAPACIDAD;" + fecha).split(";");
        if (respuesta[0].equals("OK") && respuesta.length == 3) {
            return Optional.of(Double.parseDouble(respuesta[2]));
        }
        if (respuesta[0].equals("ERROR") && respuesta.length > 1 && respuesta[1].equals("404")) {
            return Optional.empty(); //ContSocket no ha publicado capacidad para ese día
        }
        throw new ServicioExternoException("Respuesta inesperada de ContSocket: " + String.join(";", respuesta));
    }

    @Override
    public void notificarAsignacion(LocalDate fecha, int numContenedores, int totalEnvases) {
        String respuesta = enviar("NOTIFICAR_ASIGNACION;" + fecha + ";" + numContenedores + ";" + totalEnvases);
        if (!respuesta.startsWith("OK;")) {
            throw new PeticionInvalidaException("ContSocket ha rechazado la notificación: " + respuesta);
        }
    }

    //Abre la conexión, envía la petición y devuelve la respuesta del servidor
    private String enviar(String peticion) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, puerto), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);
            try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {
                out.writeUTF(peticion);
                out.flush();
                return in.readUTF();
            }
        } catch (IOException e) {
            throw new ServicioExternoException("No se ha podido contactar con ContSocket en " + host + ":" + puerto);
        }
    }
}
