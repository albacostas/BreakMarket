package com.albadiego.breakmarket.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BetOptionDto (
        UUID uuid,
        String description,
        BigDecimal currentRate,
        BigDecimal totalPool
) {}
