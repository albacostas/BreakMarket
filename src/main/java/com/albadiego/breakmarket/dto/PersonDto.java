package com.albadiego.breakmarket.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PersonDto(
        @NotNull
        UUID uuid,
        @NotNull
        String fullName
) {}
