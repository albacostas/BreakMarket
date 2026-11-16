package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.BetDto;
import com.albadiego.breakmarket.dto.EvidenceDto;
import com.albadiego.breakmarket.service.EvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.Date;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/evidence")
@Tag(name = "Pruebas", description = "Operaciones para consultar y gestionar las pruebas sober las apuestas del sistema")
@RequiredArgsConstructor
public class EvidenceController {

    private final EvidenceService evidenceService;

    @Operation(
            summary = "Obtener una lista de todas las pruebas",
            description = "Buscar todas las pruebas que cumplan con los filtros establecidos y mostrar su información"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de pruebas obtenida con éxito",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = EvidenceDto.class)
                    ))
            )
    })
    @GetMapping
    public ResponseEntity<List<EvidenceDto>> getAllEvidences(
            @Parameter(description = "Estado de la prueba", schema = @Schema(allowableValues = {"PENDING", "VALIDATED", "NOT_ACCEPTED"}), example = "PENDING", required = false)
            @RequestParam(required = false) String state,
            @Parameter(description = "UUID de la apuesta asociada a las pruebas", example = "123e4567-e89b-12d3-a456-426614174000", required = false)
            @RequestParam(required = false) UUID betID,
            @Parameter(description = "Fecha de envío de la prueba", example = "2026-11-11", required = false)
            @RequestParam(required = false) LocalDate submittedDate
    ) {
        return ResponseEntity.ok().headers(jsonHeaders())
                .body(evidenceService.findAll(state, betID, submittedDate));
    }

    @Operation(
            summary = "Obtener una prueba por su ID",
            description = "Buscar una prueba a partir de su identificador único y obtener toda su información"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Prueba encontrada con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EvidenceDto.class))
            )
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<EvidenceDto> getEvidence(
            @Parameter(description = "UUID de la prueba", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
            @PathVariable UUID uuid
    ) {
        return ResponseEntity.ok().headers(jsonHeaders()).body(evidenceService.findById(uuid));
    }

    @PatchMapping("/{uuid}/review")
    public ResponseEntity<EvidenceDto> reviewEvidence(
            @PathVariable UUID uuid,
            @RequestParam UUID reviewerUuid,
            @RequestParam String state
    ) {
        return ResponseEntity.ok().headers(jsonHeaders()).body(evidenceService.review(uuid, reviewerUuid, state));
    }

    private static HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private static URI location(UUID uuid) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(uuid)
                .toUri();
    }
}
