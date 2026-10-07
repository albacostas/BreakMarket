package com.albadiego.breakmarket.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class BetNotFoundException extends Exception {
    private final UUID uuid;
}
