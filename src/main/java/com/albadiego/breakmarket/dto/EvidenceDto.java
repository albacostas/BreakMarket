package com.albadiego.breakmarket.dto;

import java.time.Instant;
import java.util.UUID;

public record EvidenceDto (
        UUID uuid,
        UUID betUuid,
        UUID authorUuid,
        String comment,
        Instant submittedDate,
        String state
) {}