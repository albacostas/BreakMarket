package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.BetState;
import com.albadiego.breakmarket.utils.enums.BetType;

import java.time.Instant;
import java.util.UUID;

/*
    Estructura que se enviará en formato JSON
*/
public record BetDto(
        UUID uuid,
        UUID creatorUuid,
        UUID coupleUuid,
        BetType type,
        String description,
        BetState state,
        Instant startDate,
        Instant endDate
) {}