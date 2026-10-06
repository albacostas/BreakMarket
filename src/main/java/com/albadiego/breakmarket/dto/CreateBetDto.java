package com.albadiego.breakmarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/*
    Recoger los datos para publicar una nueva apuesta.
*/
public record CreateBetDto(
        @NotNull UUID creatorUuid,
        @NotNull UUID coupleUuid,
        @NotBlank String type,
        @NotBlank String description,
        Instant endDate
) {}