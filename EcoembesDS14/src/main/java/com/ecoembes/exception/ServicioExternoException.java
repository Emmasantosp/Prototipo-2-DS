package com.ecoembes.exception;

//La planta de reciclaje no responde o no se puede contactar. Se traduce a 503
public class ServicioExternoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServicioExternoException(String mensaje) {
        super(mensaje);
    }
}
