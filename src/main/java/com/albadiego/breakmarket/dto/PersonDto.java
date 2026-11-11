package com.albadiego.breakmarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PersonDto(
        @NotNull
        @Schema(description = "Identificador único de la persona", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID uuid,
        @NotNull
        @Schema(description = "Nombre completo de la persona", example = "Alba Costas")
        String fullName
) {}
