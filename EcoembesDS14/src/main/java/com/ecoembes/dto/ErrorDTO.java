package com.ecoembes.dto;

//Cuerpo de las respuestas de error (400, 401, 404)
public record ErrorDTO(int status, String error, String mensaje) {
}
