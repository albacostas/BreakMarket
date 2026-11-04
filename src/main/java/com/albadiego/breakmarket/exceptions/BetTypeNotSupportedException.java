package com.albadiego.breakmarket.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BetTypeNotSupportedException extends Exception {
    public String betType;
}
