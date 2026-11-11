package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.BetType;
import com.albadiego.breakmarket.utils.enums.ValueOfEnum;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


/*
    Recoger los datos para publicar una nueva apuesta.
*/
public record CreateBetDto(
        @NotNull(message = "El UUID del creador de la apuesta no puede ser nulo")
        @Schema(description = "Identificador único del usuario que creó la apuesta", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID creatorUuid,
        @NotNull(message = "El UUID de la pareja sobre la que se realiza la apuesta no puede ser nulo")
        @Schema(description = "Identificador único de la pareja sobre la que se realiza la apuesta", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID coupleUuid,
        @NotBlank(message = "El tipo de la apuesta no puede ser nulo")
        @ValueOfEnum(enumClass = BetType.class, message = "El tipo de la apuesta debe tomar un valor válido")
        @Schema(description = "Tipo de la apuesta", example = "YES_NO")
        String type,
        @NotBlank(message = "El título de una apuesta no puede ser nulo ni estar vacío")
        @Schema(description = "Título de la apuesta", example = "¿Durarán juntos más de un año?")
        String description,
        Instant endDate,
        @ArraySchema(schema = @Schema(description = "Descripción de la opción de la apuesta", example = "YES"))
        List<String> options
) {}