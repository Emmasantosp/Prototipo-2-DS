package com.ecoembes.dto;

import java.time.LocalDate;

import com.ecoembes.entity.NivelLlenado;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

//Body con la lectura diaria del sensor del contenedor
public record ActualizarContenedorDTO(
        @Schema(description = "Día de la lectura (AAAA-MM-DD). Si se omite, hoy.", example = "2026-11-07") LocalDate fecha,
        @Schema(description = "Nº estimado de envases", example = "640") @NotNull @PositiveOrZero Integer numEnvases,
        @Schema(example = "NARANJA") @NotNull NivelLlenado nivelLlenado) {
}
