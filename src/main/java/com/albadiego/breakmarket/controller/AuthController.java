package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.*;
import com.albadiego.breakmarket.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/rexistro")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsDto rexistrar(@RequestBody @Valid CreateUserDto datos) {
        return authService.rexistrar(datos);
    }

    @PostMapping("/login")
    public TokenResponseDto login(@RequestBody LoginDto credenciais) {
        return authService.login(credenciais);
    }

    @PostMapping("/refresh")
    public TokenResponseDto refresh(@RequestBody RefreshRequestDto request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody RefreshRequestDto request) {
        authService.logout(request.refreshToken());
    }
}