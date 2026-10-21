package com.albadiego.breakmarket.dto;

import java.time.Instant;
import java.util.UUID;

public record BetTicketDto (
        UUID uuid,
        UUID userUuid,
        UUID betUuid,
        UUID optionUuid,
        Float rate,
        Float quantity,
        String state,
        Instant createdDate

) {}
