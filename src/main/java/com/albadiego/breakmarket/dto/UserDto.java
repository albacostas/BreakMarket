package com.albadiego.breakmarket.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UserDto (
        UUID uuid,
        String username,
        String password,
        String fullName,
        String email,
        BigDecimal coins,
        String role
) {}
