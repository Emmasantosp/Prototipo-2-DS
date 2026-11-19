package com.contsocket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * Servidor de la planta de reciclaje ContSocket Ltd. (servicio externo del Prototipo 2).
 * Comunicación por sockets TCP y datos en memoria. Atiende cada conexión en un hilo propio.
 */
public class ContSocketServer {

    public static void main(String[] args) throws IOException {
        int puerto = leerPuerto(args);
        CapacidadStore store = new CapacidadStore();
        store.cargarDatosIniciales();

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("[ContSocket] Servidor escuchando en el puerto " + puerto);
            while (true) {
                Socket cliente = serverSocket.accept(); //Espera a que el Servidor Ecoembes se conecte
                new Thread(new ClienteHandler(cliente, store)).start();
            }
        }
    }

    //El puerto se lee de config.properties; si se pasa como argumento, tiene prioridad
    private static int leerPuerto(String[] args) throws IOException {
        if (args.length > 0) {
            return Integer.parseInt(args[0]);
        }
        Properties props = new Properties();
        try (InputStream in = ContSocketServer.class.getResourceAsStream("/config.properties")) {
            if (in != null) {
                props.load(in);
            }
        }
        return Integer.parseInt(props.getProperty("puerto", "9090"));
    }
}
