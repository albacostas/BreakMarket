package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.*;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.model.BetOption;
import com.albadiego.breakmarket.service.BetOptionService;
import com.albadiego.breakmarket.service.BetService;
import com.albadiego.breakmarket.service.BetTicketService;
import com.albadiego.breakmarket.service.EvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bets")
@Tag(name = "Apuestas", description = "Operaciones para consultar y gestionar las apuestas del sistema")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;
    private final BetOptionService betOptionService;
    private final BetTicketService betTicketService;
    private final EvidenceService evidenceService;

    @Operation(
            summary = "Obtener una lista de todas las apuestas",
            description = "Buscar todas las apuestas que cumplan con los filtros establecidos y mostrar su información"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de apuestas obtenida con éxito",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = BetDto.class)
                    ))
            )
    })
    @GetMapping
    public ResponseEntity<List<BetDto>> getBets(
            @Parameter(description = "UUID de la pareja involucrada en la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", required = false)
            @RequestParam(required = false) UUID coupleId,
            @Parameter(description = "UUID del usuario creador de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", required = false)
            @RequestParam(required = false) UUID authorId,
            @Parameter(description = "Título de la apuesta", example = "¿Romperán este año?", required = false)
            @RequestParam(required = false) String description,
            @Parameter(description = "Tipo de la apuesta", schema = @Schema(allowableValues = {"DATE", "PERSON", "YES_NO"}), example = "YES_NO", required = false)
            @RequestParam(required = false) String type,
            @Parameter(description = "Estado de la apuesta", schema = @Schema(allowableValues = {"ACTIVE", "CLOSED"}), example = "ACTIVE", required = false)
            @RequestParam(required = false) String state,
            @Parameter(description = "Fecha de creación de la apuesta", example = "2026-11-11", required = false)
            @RequestParam(required = false) LocalDate startDate
    ) {
        return ResponseEntity.ok().headers(jsonHeaders())
                .body(betService.filterBets(coupleId, authorId, description, type, state, startDate));
    }

    @Operation(
            summary = "Obtener una apuesta por su ID",
            description = "Buscar una apuesta a partir de su identificador único y obtener toda su información"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Apuesta encontrada con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BetDto.class))
            )
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<BetDto> getBetById(
            @Parameter(description = "UUID de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
            @PathVariable UUID uuid
    ) throws BetNotFoundException {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betService.findById(uuid));
    }

    @Operation(
            summary = "Crear una nueva apuesta",
            description = "Definir las características de una nueva apuesta y publicarla en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Apuesta creada con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BetDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<BetDto> createBet(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para la creación de la apuesta", required = true)
            @Valid @RequestBody CreateBetDto createBetDto
    ) throws UserNotFoundException, CoupleNotFoundException, BetTypeNotSupportedException, InvalidBetOptionDateException, BetOptionsNotSpecifiedException {
        BetDto created = betService.createBet(createBetDto);
        return ResponseEntity.created(location(created.uuid()))
                .headers(jsonHeaders())
                .body(created);
    }

    @Operation(
            summary = "Modificar una apuesta",
            description = "Cambiar el estado de una apuesta identificada por su UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Apuesta modificada con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BetDto.class))
            )
    })
    @PatchMapping("/{uuid}")
    public ResponseEntity<BetDto> updateBet(
            @Parameter(description = "UUID de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
            @PathVariable UUID uuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Lista de instrucciones JSON Patch", required = true, content = @Content(
                    mediaType = "application/json-patch+json",
                    array = @ArraySchema(
                            schema = @Schema(
                                    type = "object",
                                    example = """
                                        {
                                            "op": "replace",
                                            "path": "/state",
                                            "value": "CLOSED"
                                        }
                                        """
                            )
            )))
            @RequestBody List<Map<String, Object>> changes
    ) throws BetNotFoundException, UnsupportedPatchPathException, InvalidEnumStateException {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betService.updateBet(uuid, changes));
    }

    @Operation(
            summary = "Obtener las opciones de una apuesta",
            description = "Buscar todas las opciones establecidas para una apuesta identificada por su UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Opciones encontradas con éxito",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = BetOptionDto.class)
                    ))
            )
    })
    @GetMapping("/{betUuid}/options")
    public ResponseEntity<List<BetOptionDto>> getBetOptions(
            @Parameter(description = "UUID de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
            @PathVariable UUID betUuid
    ) throws BetNotFoundException {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betService.findOptionsByBet(betUuid));
    }

    @PostMapping("/{betUuid}/options")
    public ResponseEntity<BetOption> createBetOption(
            @PathVariable UUID betUuid,
            @RequestParam String description,
            @RequestParam java.math.BigDecimal currentRate) {
        BetOption created = betOptionService.create(betUuid, description, currentRate);
        return ResponseEntity.created(location(created.getUuid()))
                .headers(jsonHeaders())
                .body(created);
    }

    @Operation(
            summary = "Obtener las pruebas asociadas a una apuesta",
            description = "Buscar todas las pruebas asociadas a una apuesta identificada por su UUID, aplicando los filtros correspondientes"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pruebas encontradas con éxito",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = EvidenceDto.class)
                    ))
            )
    })
    @GetMapping("/{betUuid}/evidence")
    public ResponseEntity<List<EvidenceDto>> getBetEvidence(
            @Parameter(description = "UUID de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
            @PathVariable UUID betUuid,
            @Parameter(description = "Estado de la prueba", schema = @Schema(allowableValues = {"PENDING", "VALIDATED", "NOT_ACCEPTED"}), example = "PENDING", required = false)
            @RequestParam(required = false) String state

    ) throws InvalidEvidenceStateException {
        return ResponseEntity.ok().headers(jsonHeaders()).body(evidenceService.filterByBet(betUuid, state));
    }

    @Operation(
            summary = "Aportar una nueva prueba para resolver una apuesta",
            description = "Añadir una nueva prueba para la apuesta especificada mediante su UUID único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pruebas creada con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EvidenceDto.class))
            )
    })
    @PostMapping("/{betUuid}/evidence")
    public ResponseEntity<EvidenceDto> submitBetEvidence(
            @Parameter(description = "UUID de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
            @PathVariable UUID betUuid,
            @Valid @RequestBody CreateEvidenceDto createEvidenceDto
    ) {
        EvidenceDto created = evidenceService.submit(betUuid, createEvidenceDto);
        return ResponseEntity.created(location(created.uuid()))
                .headers(jsonHeaders())
                .body(created);
    }

    @GetMapping("/{betUuid}/options/{optionUuid}")
    public ResponseEntity<BetOptionDto> getBetOption(@PathVariable UUID optionUuid) throws BetOptionNotFoundException {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betOptionService.findById(optionUuid));
    }

    @PatchMapping("/{betUuid}/options/{optionUuid}")
    public ResponseEntity<BetOptionDto> updateBetOptionRate(
            @PathVariable UUID optionUuid,
            @RequestParam java.math.BigDecimal currentRate) throws BetOptionNotFoundException {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betOptionService.updateRate(optionUuid, currentRate));
    }

    @DeleteMapping("/{betUuid}/options/{optionUuid}")
    public ResponseEntity<Void> deleteBetOption(@PathVariable UUID optionUuid) throws BetOptionNotFoundException {
        betOptionService.delete(optionUuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar una lista de todas las apuestas realizadas para una apuesta",
            description = "Obtener una lista conteniendo todas las apuestas sobre alguna opción de una apuesta concreta"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista obtenida con éxito",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = BetTicketDto.class)
                    ))
            )
    })
    @GetMapping("/{betUuid}/tickets")
    public ResponseEntity<List<BetTicketDto>> getBetTickets(
            @Parameter(description = "UUID de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
            @PathVariable UUID betUuid,
            @Parameter(description = "Fecha de compra de la posición", example = "2026-11-11", required = false)
            @RequestParam(required = false) LocalDate date,
            @Parameter(description = "UUID del usuario que compró la posición", example = "123e4567-e89b-12d3-a456-426614174000", required = false)
            @RequestParam(required = false) UUID authorID,
            @Parameter(description = "UUID de la opción que se busca", example = "123e4567-e89b-12d3-a456-426614174000", required = false)
            @RequestParam(required = false) UUID option
    ) {
        return ResponseEntity.ok().headers(jsonHeaders())
                .body(betTicketService.findByBet(betUuid, date, authorID, option));
    }

    @Operation(
            summary = "Comprar una nueva posición sobre una opción de una apuesta concreta",
            description = "Crear un nuevo ticket mediante la compra de una opción para una empresa concreta"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Apuesta realizada con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BetTicketDto.class))
            )
    })
    @PostMapping("/{betUuid}/tickets")
    public ResponseEntity<BetTicketDto> createBetTicket(
            @Parameter(description = "UUID de la apuesta", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
            @PathVariable UUID betUuid,
            @Valid @RequestBody CreateBetTicketDto createBetTicketDto) throws BetOptionNotFoundException, BetClosedException, InvalidBetTicketQuantityException, NotEnoughCoinsException {
        BetTicketDto created = betTicketService.create(betUuid, createBetTicketDto);
        return ResponseEntity.created(location(created.uuid()))
                .headers(jsonHeaders())
                .body(created);
    }

    @GetMapping("/tickets/{ticketUuid}")
    public ResponseEntity<BetTicketDto> getBetTicket(@PathVariable UUID ticketUuid) {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betTicketService.findById(ticketUuid));
    }

    @PatchMapping("/tickets/{ticketUuid}/resolve")
    public ResponseEntity<BetTicketDto> resolveBetTicket(
            @PathVariable UUID ticketUuid,
            @RequestParam String state) {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betTicketService.resolve(ticketUuid, state));
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