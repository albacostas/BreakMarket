package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.UserDto;
import com.albadiego.breakmarket.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal Jwt principal) {
        if (!Boolean.TRUE.equals(principal.getClaim("https://breakmarket-api/email_verified"))) {
            throw new IllegalArgumentException("A verified email address is required");
        }

        return userService.findOrCreateOAuthUser(
                principal.getClaimAsString("https://breakmarket-api/email"),
                principal.getClaimAsString("https://breakmarket-api/name"),
                principal.getClaimAsString("https://breakmarket-api/nickname")
        );
    }
}
