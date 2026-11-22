package com.ecoembes.exception;

//El recurso pedido (contenedor, planta, capacidad de un día) no existe. Se traduce a 404
public class RecursoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
