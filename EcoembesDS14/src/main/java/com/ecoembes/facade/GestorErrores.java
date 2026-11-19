package com.ecoembes.facade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.ecoembes.dto.ErrorDTO;
import com.ecoembes.exception.CredencialesIncorrectasException;
import com.ecoembes.exception.PeticionInvalidaException;
import com.ecoembes.exception.RecursoNoEncontradoException;
import com.ecoembes.exception.ServicioExternoException;
import com.ecoembes.exception.TokenInvalidoException;

/** Traduce las excepciones de la aplicación a códigos HTTP (400, 401, 404, 503) para toda la fachada. */
@RestControllerAdvice
public class GestorErrores {

    @ExceptionHandler({TokenInvalidoException.class, CredencialesIncorrectasException.class})
    public ResponseEntity<ErrorDTO> noAutorizado(RuntimeException e) {
        return error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorDTO> noEncontrado(RecursoNoEncontradoException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(PeticionInvalidaException.class)
    public ResponseEntity<ErrorDTO> peticionInvalida(PeticionInvalidaException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ServicioExternoException.class)
    public ResponseEntity<ErrorDTO> servicioExterno(ServicioExternoException e) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class,
                       MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorDTO> datosInvalidos(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos: revisa parámetros, formato de fechas (AAAA-MM-DD) y body");
    }

    private ResponseEntity<ErrorDTO> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(new ErrorDTO(status.value(), status.getReasonPhrase(), mensaje));
    }
}
