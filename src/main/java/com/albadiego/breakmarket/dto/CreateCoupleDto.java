package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.CoupleState;
import com.albadiego.breakmarket.utils.enums.ValueOfEnum;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateCoupleDto(
        @NotNull(message = "El estado de la pareja no puede ser nulo")
        @ValueOfEnum(enumClass = CoupleState.class, message = "El estado de la pareja debe tomar un valor válido")
        @Schema(description = "Estado de la pareja", example = "AFFAIR")
        String status,
        Instant startDate,
        @NotEmpty(message = "La lista de miembros de la pareja no puede ser nula ni estar vacía")
        @ArraySchema(schema = @Schema(description = "Identificador único de un miembro de la pareja", example = "123e4567-e89b-12d3-a456-426614174000"))
        List<UUID> members
){ }
