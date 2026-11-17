package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.*;
import com.albadiego.breakmarket.model.RefreshToken;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.RefreshTokenRepository;
import com.albadiego.breakmarket.repository.UserRepository;
import com.albadiego.breakmarket.utils.enums.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDetailsDto rexistrar(CreateUserDto datos) {
        // 1. Comprobamos que non exista xa un usuario co mesmo username ou email
        if (usuarioRepository.existsByUsername(datos.username())) {
            throw new IllegalArgumentException("O nome de usuario xa está en uso");
        }

        // 2. Creamos a entidade e ciframos o contrasinal con passwordEncoder.encode(...)
        //    NUNCA gardamos datos.password() en texto plano!
        User novo = new User();
        novo.setUsername(datos.username());
        novo.setEmail(datos.email());
        novo.setPassword(passwordEncoder.encode(datos.password())); // Xera o hash BCrypt co seu salt
        novo.setRole(UserRole.USER); // Asignamos o rol básico por defecto
        novo.setFullName(datos.fullName());

        User gardado = usuarioRepository.save(novo);
        return new UserDetailsDto(gardado.getUuid(), gardado.getUsername(), gardado.getRole());
    }

    @Transactional
    public TokenResponseDto login(LoginDto credenciais) {
        // 1. O AuthenticationManager chama a MyUserDetailsService, comproba o hash BCrypt
        //    e verifica que o usuario estea activo. Se falla, lanza BadCredentialsException (401)
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credenciais.username(), credenciais.password())
        );

        // 2. Se chegamos aquí, o usuario é válido. Extraemos os seus roles
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .toList();

        // 3. Xeramos o Access Token (JWT de curta duración)
        String token = jwtService.generateToken(auth.getName(), roles);

        // 4. Xeramos e almacenamos o Refresh Token (opaco e de longa duración)
        String refreshToken = generateRefreshToken(auth.getName());

        return new TokenResponseDto(token, refreshToken);
    }

    // =========================================================================
    // ESQUELETO PARA A XESTIÓN DE REFRESH TOKENS
    // =========================================================================

    /**
     * Crea un novo Refresh Token opaco (UUID) e gárdao na base de datos.
     */
    private String generateRefreshToken(String username) {
        User usuario = usuarioRepository.findByUsername(username).orElseThrow();

        // (Opcional) Se só queremos permitir unha sesión activa por usuario,
        // poderiamos borrar ou revogar aquí os tokens anteriores dese usuario:
        // refreshTokenRepository.deleteByUsuario(usuario);

        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID());
        rt.setUser(usuario);
        rt.setExpiration(Instant.now().plus(7, ChronoUnit.DAYS)); // Ex: 7 días de validez
        rt.setIsInvalidated(false);

        refreshTokenRepository.save(rt);
        return rt.getToken().toString();
    }

    /**
     * Valida un Refresh Token enviado polo cliente e emite un novo par de tokens
     * aplicando "Refresh Token Rotation" (invalida o usado e xera un novo).
     */
    @Transactional
    public TokenResponseDto refresh(UUID refreshTokenRecibido) {
        // 1. Buscamos o token na BD e verificamos que non estea revogado
        RefreshToken rt = refreshTokenRepository.findByTokenAndIsInvalidatedIsFalse(refreshTokenRecibido, false)
                .orElseThrow(() -> new BadCredentialsException("Refresh token inválido ou revogado"));

        // 2. Comprobamos que non teña caducado
        if (rt.getExpiration().isBefore(Instant.now())) {
            refreshTokenRepository.delete(rt);
            throw new BadCredentialsException("O refresh token caducou. Inicie sesión de novo.");
        }

        // 3. Rotación do Refresh Token: marcamos o actual como revogado (ou borrámolo)
        //    e xeramos un novo para evitar ataques de reutilización
        rt.setIsInvalidated(true);
        refreshTokenRepository.save(rt);

        User usuario = rt.getUser();
        String novoAccessToken = jwtService.generateToken(usuario.getUsername(), List.of(usuario.getRole().toString()));
        String novoRefreshToken = generateRefreshToken(usuario.getUsername());

        return new TokenResponseDto(novoAccessToken, novoRefreshToken);
    }

    /**
     * Revoga o Refresh Token ao pechar sesión (Logout) para que non poida
     * empregarse para obter novos Access Tokens.
     */
    @Transactional
    public void logout(UUID refreshTokenRecibido) {
        refreshTokenRepository.findByTokenAndIsInvalidatedIsFalse(refreshTokenRecibido, false)
                .ifPresent(rt -> {
                    rt.setIsInvalidated(true);
                    refreshTokenRepository.save(rt);
                });
    }
}
