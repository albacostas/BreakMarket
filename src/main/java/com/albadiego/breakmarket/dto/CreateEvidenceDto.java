package com.albadiego.breakmarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateEvidenceDto(
     @NotNull
     @Schema(description = "Identificador único del usuario que envia la prueba", example = "123e4567-e89b-12d3-a456-426614174000")
     UUID authorUUID,

     @NotBlank
     @Schema(description = "Explicación de la prueba aportada", example = "Me encontré con el en un bar con otra chica")
     String comment
) { }
