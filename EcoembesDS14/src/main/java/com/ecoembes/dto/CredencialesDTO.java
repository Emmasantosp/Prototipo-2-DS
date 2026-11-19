package com.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

//Body del login
public record CredencialesDTO(
        @Schema(example = "lorea@ecoembes.com") @NotBlank String email,
        @Schema(example = "1234") @NotBlank String password) {
}
