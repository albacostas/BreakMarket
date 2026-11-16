package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.BetDto;
import com.albadiego.breakmarket.dto.CreateUserDto;
import com.albadiego.breakmarket.dto.UserDto;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.service.UserService;
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
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Operaciones para consultar y gestionar los usuarios del sistema")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Obtener una lista de todos los usuarios",
            description = "Buscar todos los usuarios que cumplan con los filtros establecidos y mostrar su información"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida con éxito",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = UserDto.class)
                    ))
            )
    })
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(
            @Parameter(description = "Nombre completo del usuario", example = "Alba Costas", required = false)
            @RequestParam(required = false) String fullName,
            @Parameter(description = "Nombre de usuario", example = "alba_costas", required = false)
            @RequestParam(required = false) String username
    ) {
        return ResponseEntity.ok(userService.findAllUsers(fullName, username));
    }

    @Operation(
            summary = "Obtener un usuario por su ID",
            description = "Buscar un usuario a partir de su identificador único y obtener toda su información"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            )
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<UserDto> getUserDetails(
            @Parameter(description = "UUID del usuario", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
            @PathVariable UUID uuid
    ) throws UserNotFoundException {
        return ResponseEntity.ok(userService.findUserByUuid(uuid));
    }

    @Operation(
            summary = "Crear un nuevo usuario",
            description = "Definir las características de un nuevo usuario y registrarlo en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para la creación del usuario", required = true)
            @Valid @RequestBody CreateUserDto newUser
    ) throws EmailAlreadyUsedException, UsernameAlreadyUsedException, GeneralConstraintViolationException {
        return ResponseEntity.ok(userService.insertUser(newUser));
    }

    @Operation(
            summary = "Modificar los datos de un usuario",
            description = "Cambiar el nombre, contraseña o rol de un usuario identificado por su UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario modificado con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            )
    })
    @PatchMapping("/{uuid}")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "UUID del usuario", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
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
    ) throws UserNotFoundException, UnsupportedPatchPathException, InvalidEnumStateException {
        return ResponseEntity.ok(userService.updateUser(uuid, changes));
    };
}
