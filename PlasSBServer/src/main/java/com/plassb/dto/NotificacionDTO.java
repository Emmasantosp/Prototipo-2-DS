package com.plassb.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

//Notificación de asignación que envía el Servidor Ecoembes
public record NotificacionDTO(
        @Schema(description = "Día en que llegan los envases (AAAA-MM-DD)", example = "2026-11-25") @NotNull LocalDate fecha,
        @Schema(example = "2") @NotNull @Positive Integer numContenedores,
        @Schema(example = "1450") @NotNull @PositiveOrZero Integer totalEnvases) {
}
