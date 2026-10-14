package com.albadiego.breakmarket.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;

@AllArgsConstructor
@Getter
public class GeneralConstraintViolationException extends Exception{
    private DataIntegrityViolationException cause;
}
