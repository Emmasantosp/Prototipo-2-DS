package com.ecoembes.dto;

//Respuesta del login: token de sesión (timestamp) y datos básicos del empleado
public record TokenDTO(String token, String nombre, String email) {
}
