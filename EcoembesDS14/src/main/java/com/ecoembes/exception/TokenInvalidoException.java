package com.ecoembes.exception;

//El token no existe o ha caducado (logout). Se traduce a 401
public class TokenInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenInvalidoException() {
        super("Token inválido o caducado. Haz login de nuevo.");
    }
}
