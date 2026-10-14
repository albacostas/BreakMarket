package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.CreateUserDto;
import com.albadiego.breakmarket.dto.UserDto;
import com.albadiego.breakmarket.exceptions.EmailAlreadyUsedException;
import com.albadiego.breakmarket.exceptions.GeneralConstraintViolationException;
import com.albadiego.breakmarket.exceptions.UserNotFoundException;
import com.albadiego.breakmarket.exceptions.UsernameAlreadyUsedException;
import com.albadiego.breakmarket.model.Bet;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.service.UserService;
import com.albadiego.breakmarket.utils.JsonPatchOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable UUID uuid) throws UserNotFoundException {
        return ResponseEntity.ok(userService.findUserByUuid(uuid));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto newUser) throws EmailAlreadyUsedException, UsernameAlreadyUsedException, GeneralConstraintViolationException {
        return ResponseEntity.ok(userService.insertUser(newUser));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID uuid, @RequestBody List<JsonPatchOperation> changes) throws UserNotFoundException {
        return ResponseEntity.ok(userService.updateUser(uuid, changes));
    };
}
