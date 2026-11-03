package com.albadiego.breakmarket.exceptions;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class InvalidEnumStateException extends Exception {
    private List<List<String>> invalidStates;

    public List<String> getErrors() {
        List<String> mensajes = new ArrayList<>();

        for(List<String> error : invalidStates) {
            mensajes.add(error.get(1) + " no es un valor válido de " + error.get(0));
        }

        return mensajes;
    }
}
