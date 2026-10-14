package com.albadiego.breakmarket.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateCoupleDto(
        @NotNull
        String status,
        @NotNull
        Instant startDate,
        @NotNull
        @NotEmpty
        List<UUID> members
){ }
