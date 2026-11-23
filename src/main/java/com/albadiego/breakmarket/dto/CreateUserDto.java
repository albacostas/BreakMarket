package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.UserRole;
import com.albadiego.breakmarket.utils.enums.ValueOfEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserDto (
        @NotBlank(message = "El nombre de usuario no puede ser nulo ni estar vacío")
        @Schema(description = "Nombre de usuario", example = "alba05")
        String username,
        @NotNull(message = "La contraseña del usuario no puede ser nula ni estar vacía")
        @Schema(description = "Hash de la contraseña del usuario", example = "edf9cf90718610ee7de53c0dcc250739239044de9ba115bb0ca6026c3e4958a5")
        String password,
        @NotBlank(message = "El nombre completo del usuario no puede ser nulo ni estar vacío")
        @Schema(description = "Nombre completo del usuario", example = "Alba Costas")
        String fullName,
        @NotBlank(message = "La dirección de correo electrónico no puede ser nula ni estar vacía")
        @Email
        @Schema(description = "Dirección de correo electrónico del usuario", example = "alba@gmail.com")
        String email,
        @NotNull(message = "El rol del usuario no puede ser nulo")
        @ValueOfEnum(enumClass = UserRole.class, message = "El rol del usuario debe tomar un valor válido")
        @Schema(description = "Rol del usuario", example = "ADMIN")
        String role
) {}
