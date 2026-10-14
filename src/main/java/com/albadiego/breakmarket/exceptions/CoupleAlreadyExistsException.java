package com.albadiego.breakmarket.exceptions;

import com.albadiego.breakmarket.model.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CoupleAlreadyExistsException extends Exception {
    private final Person personA;
    private final Person personB;
}
