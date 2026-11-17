package com.albadiego.breakmarket.dto;

import com.albadiego.breakmarket.utils.enums.UserRole;

import java.util.UUID;

public record UserDetailsDto(
        UUID uuid,
        String username,
        UserRole role
) {}
