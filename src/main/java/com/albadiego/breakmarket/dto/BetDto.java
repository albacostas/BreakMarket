package com.albadiego.breakmarket.dto;

import java.time.Instant;
import java.util.UUID;

/*
    Estructura que se enviará en formato JSON
*/
public record BetDto(
        UUID uuid,
        UUID creatorUuid,
        UUID coupleUuid,
        String type,
        String description,
        String state,
        Instant startDate,
        Instant endDate
) {}