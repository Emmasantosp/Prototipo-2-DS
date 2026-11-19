package com.ecoembes.exception;

//Datos de entrada incorrectos o regla de negocio incumplida. Se traduce a 400
public class PeticionInvalidaException extends RuntimeException {
    public PeticionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
