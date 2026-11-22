package com.plassb.exception;

import java.time.LocalDate;

//La planta no ha publicado capacidad para la fecha pedida. Se traduce a 404
public class CapacidadNoPublicadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CapacidadNoPublicadaException(LocalDate fecha) {
        super("No hay capacidad publicada para " + fecha);
    }
}
