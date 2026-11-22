package com.ecoembes.exception;

//Email o contraseña incorrectos en el login. Se traduce a 401
public class CredencialesIncorrectasException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CredencialesIncorrectasException() {
        super("Email o contraseña incorrectos");
    }
}
