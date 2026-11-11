package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.EvidenceState;

import java.time.Instant;
import java.util.UUID;

public record EvidenceDto (
        UUID uuid,
        UUID betUuid,
        UUID authorUuid,
        String comment,
        Instant submittedDate,
        EvidenceState state
) {}