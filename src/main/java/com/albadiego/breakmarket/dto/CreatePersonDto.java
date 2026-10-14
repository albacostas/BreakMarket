package com.albadiego.breakmarket.dto;

import jakarta.validation.constraints.NotNull;

public record CreatePersonDto(
        @NotNull
        String fullName
) {}
