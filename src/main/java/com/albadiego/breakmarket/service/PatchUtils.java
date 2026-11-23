package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.utils.exceptions.JsonPatchFailedException;
import com.albadiego.breakmarket.utils.patch.JsonPatch;
import com.albadiego.breakmarket.utils.patch.JsonPatchOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PatchUtils {
    private final ObjectMapper mapper;

    public <T> T applyPatch(T data, List<Map<String, Object>> updates) throws JsonPatchFailedException {
        // Convertir la lista de mapas a un objeto JSON Patch
        List<JsonPatchOperation> operations = updates.stream().map(it -> mapper.convertValue(it, JsonPatchOperation.class)).toList();

        // Convertir el objeto data a un JSON Node
        JsonNode json = mapper.convertValue(data, JsonNode.class);

        // Aplicar las operaciones al JSON Node
        JsonNode updatedJson = JsonPatch.apply(operations, json);

        // Convertir el JSON Node de vuelta al objeto de dominio
        return (T) mapper.convertValue(updatedJson, data.getClass());
    }
}
