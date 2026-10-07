package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.exceptions.BetNotFoundException;
import com.albadiego.breakmarket.exceptions.CoupleNotFoundException;
import com.albadiego.breakmarket.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController {

    // Captura las excepciones cuando no se encuentra una apuesta
    @ExceptionHandler(BetNotFoundException.class)
    public ResponseEntity<String> handleBetNotFoundException(BetNotFoundException exception) {
        return new ResponseEntity<>("Apuesta no encontrada con UUID: " +  exception.getUuid(), HttpStatus.NOT_FOUND);
    }

    // Captura las excepciones cuando no se encuentra una pareja
    @ExceptionHandler(CoupleNotFoundException.class)
    public ResponseEntity<String> handleCoupleNotFoundException(BetNotFoundException exception) {
        return new ResponseEntity<>("Pareja no encontrada con UUID: " +  exception.getUuid(), HttpStatus.NOT_FOUND);
    }

    // Captura las excepciones cuando no se encuentra un usuario
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleBetNotFoundException(UserNotFoundException exception) {
        return new ResponseEntity<>("Usuario no encontrado con UUID: " +  exception.getUuid(), HttpStatus.NOT_FOUND);
    }
}
