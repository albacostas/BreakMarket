package com.albadiego.breakmarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record BetOptionDto (
        @Schema(description = "Identificador único de la opción dentro de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID uuid,
        @Schema(description = "Descripción de la opción de la apuesta", example = "YES")
        String description,
        @Schema(description = "Tasa actual para la opción de la apuesta", example = "0.603")
        BigDecimal currentRate,
        @Schema(description = "Número total de tickets de esta opción comprados", example = "37")
        Integer totalPool
) {}
