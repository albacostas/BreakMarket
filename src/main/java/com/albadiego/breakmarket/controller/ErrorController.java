package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.utils.exceptions.JsonPatchFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    // Capturar las excepciones cuando se recibe un JSON Patch para un path no válido
    @ExceptionHandler(UnsupportedPatchPathException.class)
    public ResponseEntity<String> handleUnsupportedPatchPathException(UnsupportedPatchPathException exception) {
        return new ResponseEntity<>("No está permitido modificar los siguientes atributos: " + exception.getPaths().toString(), HttpStatus.UNPROCESSABLE_CONTENT);
    }

    // Capturar las excepciones cuando se recibe un JSON Patch para un estado no válido
    @ExceptionHandler(InvalidEnumStateException.class)
    public ResponseEntity<String> handleInvalidEnumStateException(InvalidEnumStateException exception) {
        return new ResponseEntity<>("Los valores de estado indicados no son válidos: \n" + exception.getErrors(), HttpStatus.UNPROCESSABLE_CONTENT);
    }

    // Capturar las excepciones cuando se recibe un tipo de apuesta inválido
    @ExceptionHandler(BetTypeNotSupportedException.class)
    public ResponseEntity<String> handleBetTypeNotSupportedException(BetTypeNotSupportedException exception) {
        return new ResponseEntity<>("El tipo de apuesta especificado ("+ exception.getBetType() + ") no es válido. Debe ser YES_NO, DATE o PERSON.", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    // Capturar las excepciones cuando se recibe un JSON Patch para un estado no válido
    @ExceptionHandler(InvalidBetOptionDateException.class)
    public ResponseEntity<String> handleInvalidBetOptionDateException(InvalidBetOptionDateException exception) {
        return new ResponseEntity<>("Alguna de las fechas especificadas no son válidas: " + exception.getInvalidDates() + "\n", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    // Capturar las excepciones cuando no se reciben las opciones de apuestas requeridas
    @ExceptionHandler(BetOptionsNotSpecifiedException.class)
    public ResponseEntity<String> handleBetOptionsNotSpecifiedException(BetOptionsNotSpecifiedException exception) {
        return new ResponseEntity<>("No se ha especificado ninguna opción para la apuesta", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    // Capturar las excepciones cuando se intenta crear un ticket para una apuesta cerrada
    @ExceptionHandler(BetClosedException.class)
    public ResponseEntity<String> handleBetClosedException(BetClosedException exception) {
        return new ResponseEntity<>("No se pueden realizar apuestas para una apuesta ya finalizada: " + exception.getBetUUID() + "\n", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    // Capturar las excepciones cuando se intenta realizar una apuesta con una cantidad inválida
    @ExceptionHandler(InvalidBetTicketQuantityException.class)
    public ResponseEntity<String> handleInvalidBetTicketQuantityException(InvalidBetTicketQuantityException exception) {
        return new ResponseEntity<>("No se puede realizar una apuesta con la cantidad especificada: " + exception.getQuantity() + "\n", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    // Capturar las excepciones cuando se intenta realizar una apuesta sin los fondos suficientes
    @ExceptionHandler(NotEnoughCoinsException.class)
    public ResponseEntity<String> handleNotEnoughCoinsException(NotEnoughCoinsException exception) {
        return new ResponseEntity<>("No se puede procesar la apuesta, ya que se requieren " + exception.getPurchaseCoins() + " monedas y solo tiene " + exception.getAvailableCoins() + " disponibles\n", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    // Capturar las excepciones cuando se especifica un estado de prueba que no es válido
    @ExceptionHandler(InvalidEvidenceStateException.class)
    public ResponseEntity<String> handleInvalidEvidenceStateException(InvalidEvidenceStateException exception) {
        return new ResponseEntity<>("El estado " + exception.getState() + " no es un valor válido de estado para las pruebas\n", HttpStatus.UNPROCESSABLE_CONTENT);
    }

    // Manejar los errores de Validación de Hibernate
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }
}
