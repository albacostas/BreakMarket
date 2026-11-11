package com.albadiego.breakmarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreatePersonDto(
        @NotNull
        @Schema(description = "Nombre completo de la persona", example = "Alba Costas")
        String fullName
) {}
