package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateUserDto (
        @Schema(description = "Nombre de usuario", example = "alba05")
        String username,
        @Schema(description = "Hash de la contraseña del usuario", example = "edf9cf90718610ee7de53c0dcc250739239044de9ba115bb0ca6026c3e4958a5")
        String password,
        @Schema(description = "Nombre completo del usuario", example = "Alba Costas")
        String fullName,
        @Schema(description = "Dirección de correo electrónico del usuario", example = "alba@gmail.com")
        String email,
        @Schema(description = "Rol del usuario", example = "ADMIN")
        UserRole role
) {}
