package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.exceptions.BetNotFoundException;
import com.albadiego.breakmarket.exceptions.BetOptionNotFoundException;
import com.albadiego.breakmarket.exceptions.BetTicketNotFoundException;
import com.albadiego.breakmarket.exceptions.CoupleNotFoundException;
import com.albadiego.breakmarket.exceptions.EvidenceNotFoundException;
import com.albadiego.breakmarket.exceptions.UserNotFoundException;
import com.albadiego.breakmarket.utils.exceptions.JsonPatchFailedException;
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

    // Captura las excepciones cuando no se encuentra una opción de apuesta
    @ExceptionHandler(BetOptionNotFoundException.class)
    public ResponseEntity<String> handleBetOptionNotFoundException(BetOptionNotFoundException exception) {
        return new ResponseEntity<>("Opción de apuesta no encontrada con UUID: " + exception.getUuid(), HttpStatus.NOT_FOUND);
    }

    // Captura las excepciones cuando no se encuentra un ticket de apuesta
    @ExceptionHandler(BetTicketNotFoundException.class)
    public ResponseEntity<String> handleBetTicketNotFoundException(BetTicketNotFoundException exception) {
        return new ResponseEntity<>("Ticket de apuesta no encontrado con UUID: " + exception.getUuid(), HttpStatus.NOT_FOUND);
    }

    // Captura las excepciones cuando no se encuentra una evidencia
    @ExceptionHandler(EvidenceNotFoundException.class)
    public ResponseEntity<String> handleEvidenceNotFoundException(EvidenceNotFoundException exception) {
        return new ResponseEntity<>("Evidencia no encontrada con UUID: " + exception.getUuid(), HttpStatus.NOT_FOUND);
    }

    // Captura los errores de argumentos inválidos enviados a los servicios
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Captura los errores de operaciones no permitidas por el estado del recurso
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    // Captura los errores al aplicar un JSON Patch
    @ExceptionHandler(JsonPatchFailedException.class)
    public ResponseEntity<String> handleJsonPatchFailedException(JsonPatchFailedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Captura las excepciones cuando no se encuentra una pareja
    @ExceptionHandler(CoupleNotFoundException.class)
    public ResponseEntity<String> handleCoupleNotFoundException(CoupleNotFoundException exception) {
        return new ResponseEntity<>("Pareja no encontrada con UUID: " +  exception.getUuid(), HttpStatus.NOT_FOUND);
    }

    // Captura las excepciones cuando no se encuentra un usuario
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleBetNotFoundException(UserNotFoundException exception) {
        return new ResponseEntity<>("Usuario no encontrado con UUID: " +  exception.getUuid(), HttpStatus.NOT_FOUND);
    }
}
