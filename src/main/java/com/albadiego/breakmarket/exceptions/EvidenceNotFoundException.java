package com.albadiego.breakmarket.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class EvidenceNotFoundException extends RuntimeException {
    private final UUID uuid;

    public EvidenceNotFoundException(UUID uuid) {
        super("Evidence not found: " + uuid);
        this.uuid = uuid;
    }
}
