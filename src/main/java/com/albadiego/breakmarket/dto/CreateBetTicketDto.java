package com.albadiego.breakmarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CreateBetTicketDto(
        @Schema(description = "Identificador único del usuario que compra el ticket", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userUUID,
        @Schema(description = "Identificador único de la opción dentro de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID optionUUID,
        @Schema(description = "Número de posiciones que se compran a la vez", example = "2")
        Integer quantity
) { }
