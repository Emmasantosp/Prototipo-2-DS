package com.ecoembes.dto;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

//Body de la asignación de contenedores a una planta
public record AsignacionDTO(
        @Schema(example = "PL-01") @NotBlank String idPlanta,
        @Schema(description = "Día en que la planta recibe los envases (AAAA-MM-DD)", example = "2026-11-08") @NotNull LocalDate fecha,
        @Schema(example = "[\"CNT-BIO-001\", \"CNT-BIO-002\"]") @NotEmpty List<String> idsContenedores) {
}
