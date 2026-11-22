package com.plassb.dto;

//Cuerpo de las respuestas de error (400, 404)
public record ErrorDTO(int status, String error, String mensaje) {
}
