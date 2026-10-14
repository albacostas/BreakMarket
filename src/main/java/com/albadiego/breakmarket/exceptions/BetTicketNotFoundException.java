package com.albadiego.breakmarket.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BetTicketNotFoundException extends RuntimeException {
    private final UUID uuid;

    public BetTicketNotFoundException(UUID uuid) {
        super("Bet ticket not found: " + uuid);
        this.uuid = uuid;
    }
}
