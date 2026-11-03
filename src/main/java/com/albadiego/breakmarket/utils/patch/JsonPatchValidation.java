package com.albadiego.breakmarket.utils.patch;

import com.albadiego.breakmarket.exceptions.InvalidEnumStateException;
import com.albadiego.breakmarket.exceptions.UnsupportedPatchPathException;
import com.albadiego.breakmarket.utils.enums.BetState;
import com.albadiego.breakmarket.utils.enums.CoupleState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonPatchValidation {

    public static List<String> validateOperations(List<String> validOperations, List<Map<String, Object>> changes) {
        List<String> invalidOperations = new ArrayList<>();

        for(Map<String, Object> change: changes){
            // Obtener la operación
            String operation = change.get("op").toString();

            if(!validOperations.contains(operation)){
                invalidOperations.add(operation);
            }
        }

        return invalidOperations;
    }

    public static List<String> validatePaths(List<String> validPaths, List<Map<String, Object>> changes) {
        List<String> invalidPaths = new ArrayList<>();

        for(Map<String, Object> change: changes){
            // Obtener la operación
            String path = change.get("path").toString();

            if(!validPaths.contains(path)){
                invalidPaths.add(path);
            }
        }

        return invalidPaths;
    }

    public static <T extends Enum<T>> List<List<String>> validateState(List<Map<String, Object>> changes, Class<T> type, String field) {
        List<List<String>> invalidStates = new ArrayList<>();

        for(Map<String, Object> change: changes){
            // Obtener el valor
            String value = change.get("value").toString();
            String path = change.get("path").toString();

            // Comprobar si el campo es del estado a validar
            if(path.equals(field)){
                // Comprobar si el estado es válido
                try {
                    T state = Enum.valueOf(type, value);
                } catch (IllegalArgumentException e) {
                    // Si no es válido, añadirlo a la lista
                    invalidStates.add(List.of(path, value));
                }
            }
        }

        return invalidStates;
    }
}
