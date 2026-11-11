package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.BetState;
import com.albadiego.breakmarket.utils.enums.BetType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/*
    Estructura que se enviará en formato JSON
*/
public record BetDto(
        @Schema(description = "Identificador único de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID uuid,
        @Schema(description = "Identificador único del usuario que creó la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID creatorUuid,
        @Schema(description = "Identificador único de la pareja sobre la que se realiza la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID coupleUuid,
        @Schema(description = "Tipo de la apuesta", example = "YES_NO")
        BetType type,
        @Schema(description = "Título de la apuesta", example = "¿Durarán juntos más de un año?")
        String description,
        @Schema(description = "Estado de la apuesta", example = "ACTIVE")
        BetState state,
        // TODO - Comprobar documentación de fechas
        Instant startDate,
        Instant endDate
) {}