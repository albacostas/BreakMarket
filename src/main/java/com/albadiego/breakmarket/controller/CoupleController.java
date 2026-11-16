package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.BetDto;
import com.albadiego.breakmarket.dto.CoupleDto;
import com.albadiego.breakmarket.dto.CreateCoupleDto;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.model.Couple;
import com.albadiego.breakmarket.service.CoupleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/couples")
@Tag(name = "Parejas", description = "Operaciones para consultar y gestionar las parejas del sistema")
@AllArgsConstructor
public class CoupleController {
    private final CoupleService coupleService;

    @Operation(
            summary = "Obtener una lista de todas las parejas",
            description = "Buscar todas las parejas que contengan los miembros especificados y mostrar su información"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de parejas obtenida con éxito",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = CoupleDto.class)
                    ))
            )
    })
    @GetMapping
    public ResponseEntity<List<CoupleDto>> getCouples(
            @Parameter(description = "Nombre del primer miembro de la pareja", example = "Juan", required = false)
            @RequestParam(required = false) String firstMember,
            @Parameter(description = "Nombre del segundo miembro de la pareja", example = "Alba", required = false)
            @RequestParam(required = false) String secondMember
    ) {
        return ResponseEntity.ok(coupleService.findCouples(firstMember, secondMember));
    }

    @Operation(
            summary = "Obtener la información detallada de una pareja",
            description = "Buscar una pareja por su identificador único y mostar sus detalles"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pareja encontrada con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CoupleDto.class))
            )
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<CoupleDto> getCoupleDetails(
            @Parameter(description = "UUID de la pareja", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
            @PathVariable UUID uuid
    ) throws CoupleNotFoundException {
        return ResponseEntity.ok(coupleService.findCoupleByUuid(uuid));
    }

    @Operation(
            summary = "Crear una nueva pareja",
            description = "Definir los miembros de una nueva pareja y registrarla en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pareja creada con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CoupleDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<CoupleDto> createCouple(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para la creación de la pareja", required = true)
            @Valid @RequestBody CreateCoupleDto newCouple
    ) throws PersonNotFoundException, CoupleAlreadyExistsException, GeneralConstraintViolationException {
        return ResponseEntity.ok(coupleService.insertCouple(newCouple));
    }

    @Operation(
            summary = "Modificar el estado de una pareja",
            description = "Cambiar el estado de una pareja identificada por su UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pareja modificada con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CoupleDto.class))
            )
    })
    @PatchMapping("/{uuid}")
    public ResponseEntity<CoupleDto> updateCoupleStatus(
            @Parameter(description = "UUID de la pareja", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
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
    ) throws CoupleNotFoundException, UnsupportedPatchPathException, InvalidEnumStateException {
        return ResponseEntity.ok(coupleService.updateCouple(uuid, changes));
    };
}
