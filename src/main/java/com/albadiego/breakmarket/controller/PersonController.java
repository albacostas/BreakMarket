package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.BetDto;
import com.albadiego.breakmarket.dto.CreatePersonDto;
import com.albadiego.breakmarket.dto.PersonDto;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.service.PersonService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/people")
@Tag(name = "Personas", description = "Operaciones para consultar y gestionar las personas que forman las parejas del sistema")
@AllArgsConstructor
public class PersonController {
    private final PersonService personService;

    @Operation(
            summary = "Obtener una lista de todas las personas",
            description = "Buscar todas las personas que cumplan con los filtros establecidos y mostrar su información"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de personas obtenida con éxito",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = PersonDto.class)
                    ))
            )
    })
    @GetMapping
    public ResponseEntity<List<PersonDto>> getAllPeople(
            @Parameter(description = "Nombre de la persona", example = "Alba", required = false)
            @RequestParam(required = false) String fullName
    ) {
        return ResponseEntity.ok(personService.findAllPeople(fullName));
    }

    @Operation(
            summary = "Obtener una persona por su ID",
            description = "Buscar una persona a partir de su identificador único y obtener toda su información"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Persona encontrada con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDto.class))
            )
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<PersonDto> getPersonDetails(
            @Parameter(description = "UUID de la persona", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
            @PathVariable UUID uuid
    ) throws PersonNotFoundException {
        return ResponseEntity.ok(personService.findPersonByUuid(uuid));
    }

    @Operation(
            summary = "Crear una nueva persona",
            description = "Definir las características de una nueva persona y añadirla al sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Persona creada con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<PersonDto> createPerson(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para la creación de la persona", required = true)
            @Valid @RequestBody CreatePersonDto newUser
    ) throws PersonAlreadyExistsException, GeneralConstraintViolationException {
        return ResponseEntity.ok(personService.insertPerson(newUser));
    }
}
