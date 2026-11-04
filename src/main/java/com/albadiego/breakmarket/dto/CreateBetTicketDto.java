package com.albadiego.breakmarket.dto;

import java.util.UUID;

public record CreateBetTicketDto(
        UUID userUUID,
        UUID optionUUID,
        Integer quantity
) { }
