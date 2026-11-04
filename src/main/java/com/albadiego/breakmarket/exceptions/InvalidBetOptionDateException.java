package com.albadiego.breakmarket.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class InvalidBetOptionDateException extends Exception {
    public List<String> invalidDates;
}
