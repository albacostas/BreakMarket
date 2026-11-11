package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.EvidenceState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record EvidenceDto (
        @Schema(description = "Identificador único de la prueba", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID uuid,
        @Schema(description = "Identificador único de la apuesta a la que se aporta la prueba", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID betUuid,
        @Schema(description = "Identificador único del usuario que envia la prueba", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID authorUuid,
        @Schema(description = "Explicación de la prueba aportada", example = "Me encontré con el en un bar con otra chica")
        String comment,
        Instant submittedDate,
        @Schema(description = "Estado de revisión en el que se encuentra la prueba", example = "PENDING")
        EvidenceState state
) {}