package com.albadiego.breakmarket.dto;

public record CreateUserDto (
        String username,
        String password,
        String fullName,
        String email,
        String role
) {}
