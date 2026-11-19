package com.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//Body para dar de alta un contenedor
public record CrearContenedorDTO(
        @Schema(example = "CNT-BIO-004") @NotBlank String id,
        @Schema(example = "Calle Licenciado Poza 30") @NotBlank String ubicacion,
        @Schema(example = "48011") @NotBlank String codigoPostal,
        @Schema(description = "Capacidad inicial en nº de envases", example = "1000") @NotNull @Positive Integer capacidad) {
}
