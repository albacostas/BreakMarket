package com.albadiego.breakmarket.exceptions;

import com.albadiego.breakmarket.model.Person;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class PersonAlreadyExistsException extends Exception {
    private Person person;
}
