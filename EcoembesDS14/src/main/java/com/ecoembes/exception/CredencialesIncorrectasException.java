package com.ecoembes.exception;

//Email o contraseña incorrectos en el login. Se traduce a 401
public class CredencialesIncorrectasException extends RuntimeException {
    public CredencialesIncorrectasException() {
        super("Email o contraseña incorrectos");
    }
}
