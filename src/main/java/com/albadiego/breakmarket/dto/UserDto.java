package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record UserDto (
        @Schema(description = "Identificador único del usuario", example = "123e4567-e89b-12d3-a456-426614174000", accessMode = Schema.AccessMode.READ_ONLY)
        UUID uuid,
        @Schema(description = "Nombre de usuario", example = "alba05")
        String username,
        @Schema(description = "Hash de la contraseña del usuario", example = "edf9cf90718610ee7de53c0dcc250739239044de9ba115bb0ca6026c3e4958a5")
        String password,
        @Schema(description = "Nombre completo del usuario", example = "Alba Costas")
        String fullName,
        @Schema(description = "Dirección de correo electrónico del usuario", example = "alba@gmail.com")
        String email,
        @Schema(description = "Cantidad de monedas en la cuenta del usuario", example = "1000")
        BigDecimal coins,
        @Schema(description = "Rol del usuario", example = "ADMIN")
        UserRole role
) {}
