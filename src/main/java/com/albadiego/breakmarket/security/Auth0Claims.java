package com.albadiego.breakmarket.security;

import com.albadiego.breakmarket.utils.enums.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public final class Auth0Claims {
    private static final String NAMESPACE = "https://breakmarket-api/";

    private Auth0Claims() {
    }

    public static String requireSubject(Jwt principal) {
        String subject = principal.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new AccessDeniedException("A valid Auth0 subject is required");
        }
        return subject;
    }

    public static String requireVerifiedEmail(Jwt principal) {
        if (!Boolean.TRUE.equals(principal.getClaim(NAMESPACE + "email_verified"))) {
            throw new AccessDeniedException("A verified email address is required");
        }

        String email = principal.getClaimAsString(NAMESPACE + "email");
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+$")) {
            throw new AccessDeniedException("A valid email address is required");
        }
        return email.trim();
    }

    public static UserRole requireRole(Jwt principal) {
        Object claim = principal.getClaims().get(NAMESPACE + "roles");
        if (!(claim instanceof List<?> roles) || roles.isEmpty()) {
            throw new AccessDeniedException("A valid Auth0 roles claim is required");
        }

        EnumSet<UserRole> parsedRoles = EnumSet.noneOf(UserRole.class);
        for (Object role : roles) {
            if (!(role instanceof String roleName) || roleName.isBlank()) {
                throw new AccessDeniedException("The Auth0 roles claim contains an invalid role");
            }
            try {
                parsedRoles.add(UserRole.valueOf(roleName.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException exception) {
                throw new AccessDeniedException("The Auth0 roles claim contains an unsupported role");
            }
        }

        return parsedRoles.contains(UserRole.ADMIN) ? UserRole.ADMIN : UserRole.USER;
    }
}
