package com.albadiego.breakmarket.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class NotEnoughCoinsException extends Exception {
    BigDecimal availableCoins;
    BigDecimal purchaseCoins;

}
