package com.albadiego.breakmarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePersonDto(
        @NotBlank(message = "El nombre completo de la persona no puede ser nulo ni estar vacío")
        @Schema(description = "Nombre completo de la persona", example = "Alba Costas")
        String fullName
) {}
