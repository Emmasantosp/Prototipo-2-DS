package com.plassb.facade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.plassb.dto.ErrorDTO;
import com.plassb.exception.CapacidadNoPublicadaException;

/** Traduce las excepciones a códigos HTTP (400, 404) con un ErrorDTO en JSON. */
@RestControllerAdvice
public class GestorErrores {

    @ExceptionHandler(CapacidadNoPublicadaException.class)
    public ResponseEntity<ErrorDTO> noEncontrado(CapacidadNoPublicadaException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class,
                       MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorDTO> datosInvalidos(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "Datos inválidos: revisa la fecha (AAAA-MM-DD) y el body");
    }

    private ResponseEntity<ErrorDTO> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(new ErrorDTO(status.value(), status.getReasonPhrase(), mensaje));
    }
}
