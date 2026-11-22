package com.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

//Body con la lectura diaria del sensor del contenedor: solo el nº estimado de envases.
//La fecha es la de hoy y el nivel de llenado lo calcula el servidor.
public record ActualizarContenedorDTO(
        @Schema(description = "Nº estimado de envases", example = "640") @NotNull @PositiveOrZero Integer numEnvases) {
}
