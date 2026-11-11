package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.BetType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


/*
    Recoger los datos para publicar una nueva apuesta.
*/
public record CreateBetDto(
        @NotNull
        @Schema(description = "Identificador único del usuario que creó la apuesta", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID creatorUuid,
        @NotNull
        @Schema(description = "Identificador único de la pareja sobre la que se realiza la apuesta", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID coupleUuid,
        @NotBlank
        @Schema(description = "Tipo de la apuesta", example = "YES_NO")
        String type,
        @NotBlank
        @Schema(description = "Título de la apuesta", example = "¿Durarán juntos más de un año?")
        String description,
        Instant endDate,
        @ArraySchema(schema = @Schema(description = "Descripción de la opción de la apuesta", example = "YES"))
        List<String> options
) {}