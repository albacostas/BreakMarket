package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.TicketState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record BetTicketDto (
        @Schema(description = "Identificador único del ticket de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID uuid,
        @Schema(description = "Identificador único del usuario que compró el ticket", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID userUuid,
        @Schema(description = "Identificador único de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID betUuid,
        @Schema(description = "Identificador único de la opción dentro de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID optionUuid,
        @Schema(description = "Tasa a la cual se compró esta opción", example = "0.4")
        Float rate,
        @Schema(description = "Número de posiciones que se compraron a la vez", example = "2")
        Integer quantity,
        @Schema(description = "Estado del ticket", example = "PENDING")
        TicketState state,
        // TODO - Comprobar fechas
        Instant createdDate

) {}
