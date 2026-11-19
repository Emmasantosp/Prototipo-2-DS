package com.ecoembes.gateway;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import com.ecoembes.exception.PeticionInvalidaException;
import com.ecoembes.exception.ServicioExternoException;

/**
 * Service Gateway de ContSocket Ltd.: se comunica con su servidor mediante sockets TCP.
 * Protocolo de texto (writeUTF/readUTF), una petición por conexión:
 *   CAPACIDAD;AAAA-MM-DD                     -> OK;toneladas | SIN_CAPACIDAD | ERROR;mensaje
 *   NOTIFICAR;AAAA-MM-DD;contenedores;envases;toneladas -> OK | ERROR;mensaje
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
        String respuesta = enviar("CAPACIDAD;" + fecha);
        if (respuesta.startsWith("OK;")) {
            return Optional.of(Double.parseDouble(respuesta.substring(3)));
        }
        if (respuesta.equals("SIN_CAPACIDAD")) {
            return Optional.empty();
        }
        throw new ServicioExternoException("Respuesta inesperada de ContSocket: " + respuesta);
    }

    @Override
    public void notificarAsignacion(LocalDate fecha, int numContenedores, int totalEnvases, double toneladas) {
        String peticion = String.format(Locale.ROOT, "NOTIFICAR;%s;%d;%d;%.3f", fecha, numContenedores, totalEnvases, toneladas);
        String respuesta = enviar(peticion);
        if (!respuesta.equals("OK")) {
            throw new PeticionInvalidaException("ContSocket ha rechazado la asignación: " + respuesta);
        }
    }

    //Abre la conexión, envía la petición y devuelve la respuesta del servidor
    private String enviar(String peticion) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, puerto), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out.writeUTF(peticion);
            out.flush();
            return in.readUTF();
        } catch (IOException e) {
            throw new ServicioExternoException("No se ha podido contactar con ContSocket en " + host + ":" + puerto);
        }
    }
}
