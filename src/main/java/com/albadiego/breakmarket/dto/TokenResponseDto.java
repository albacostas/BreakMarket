package com.albadiego.breakmarket.dto;

public record TokenResponseDto(
        String token,
        String refreshToken
) {}
