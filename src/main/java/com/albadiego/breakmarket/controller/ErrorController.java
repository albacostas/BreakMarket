package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.exceptions.*;
import jakarta.validation.constraints.Email;
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

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<String> handleEmailAlreadyUsedException(EmailAlreadyUsedException exception) {
        return new ResponseEntity<>("Ya existe un usuario registrado con el email: " +  exception.getEmail(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    public ResponseEntity<String> handleUsernameAlreadyUsedException(UsernameAlreadyUsedException exception) {
        return new ResponseEntity<>("Ya existe un usuario registrado con el nombre de usuario: " +  exception.getUsername(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CoupleAlreadyExistsException.class)
    public ResponseEntity<String> handleCoupleAlreadyExistsException(CoupleAlreadyExistsException exception) {
        return new ResponseEntity<>("Ya existe una pareja entre '" +  exception.getPersonA().getFullName() + "' y '" + exception.getPersonB().getFullName() + "'.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(GeneralConstraintViolationException.class)
    public ResponseEntity<String> handleGeneralConstraintViolationException(GeneralConstraintViolationException exception) {
        return new ResponseEntity<>("Conflicto de integridad en la base de datos: " + exception.getMessage(), HttpStatus.CONFLICT);
    }
}
