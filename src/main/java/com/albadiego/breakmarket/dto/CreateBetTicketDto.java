package com.albadiego.breakmarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBetTicketDto(
        @NotNull(message = "El UUID del usuario que realiza la apuesta no puede ser nulo")
        @Schema(description = "Identificador único del usuario que compra el ticket", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userUUID,
        @NotNull(message = "El UUID de la opción por la que se apuesta no puede ser nulo")
        @Schema(description = "Identificador único de la opción dentro de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID optionUUID,
        @NotNull(message = "La cantidad de posiciones por las que se apuesta no puede ser nula")
        @Min(value = 1, message = "Como mínimo se debe pujar por 1 posición")
        @Schema(description = "Número de posiciones que se compran a la vez", example = "2")
        Integer quantity
) { }
