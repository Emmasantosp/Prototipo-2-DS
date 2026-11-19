package com.ecoembes.dto;

import java.time.LocalDate;

import com.ecoembes.entity.NivelLlenado;

//Una lectura del histórico de uso de un contenedor
public record RegistroUsoDTO(LocalDate fecha, int numEnvases, NivelLlenado nivelLlenado) {
}
