package com.albadiego.breakmarket.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BetOptionNotFoundException extends RuntimeException {
    private final UUID uuid;

    public BetOptionNotFoundException(UUID uuid) {
        super("Bet option not found: " + uuid);
        this.uuid = uuid;
    }
}
