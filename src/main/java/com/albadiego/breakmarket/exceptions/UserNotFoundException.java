package com.albadiego.breakmarket.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserNotFoundException extends RuntimeException {
    private final UUID uuid;
}
