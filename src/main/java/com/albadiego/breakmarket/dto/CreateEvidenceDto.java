package com.albadiego.breakmarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateEvidenceDto(
     @NotNull
     UUID authorUUID,

     @NotBlank
     String comment
) { }
