package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.UserDto;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.UserRepository;
import com.albadiego.breakmarket.utils.enums.UserRole;
import com.albadiego.breakmarket.utils.patch.JsonPatchValidation;
import com.albadiego.breakmarket.utils.specifications.UserSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper mapper;
    private final PatchUtils patchUtils;

    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers(String fullName, String username) {
        // Crear el filtro de personas por nombre
        Specification<User> fullNameFilter = UserSpecification.userNameEquals(fullName);
        Specification<User> usernameFilter = UserSpecification.userUsernameEquals(username);

        Specification<User> specification = Specification.where(fullNameFilter).and(usernameFilter);

        List<User> results = userRepository.findAll(specification);

        // Devolver los resultados como DTO
        return results
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto findUserByUuid(UUID uuid) throws UserNotFoundException {
        return userRepository
                .findById(uuid)
                .map(this::toDto)
                .orElseThrow( () -> new UserNotFoundException(uuid) );
    }

    @Transactional
    public UserDto findOrCreateOAuthUser(String email, String name, String preferredUsername) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("The OAuth provider did not return an email address");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        int separator = normalizedEmail.indexOf('@');
        if (separator <= 0 || separator == normalizedEmail.length() - 1) {
            throw new IllegalArgumentException("The OAuth provider returned an invalid email address");
        }

        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .map(this::toDto)
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(normalizedEmail);
                    user.setFullName(name == null || name.isBlank() ? normalizedEmail : name.trim());
                    user.setUsername(uniqueUsername(preferredUsername, normalizedEmail));
                    user.setRole(UserRole.USER);
                    return toDto(userRepository.save(user));
                });
    }

    private String uniqueUsername(String preferredUsername, String email) {
        String base = preferredUsername == null || preferredUsername.isBlank()
                ? email.substring(0, email.indexOf('@'))
                : preferredUsername;
        base = base.trim().replaceAll("[^A-Za-z0-9._-]", "-")
                .replaceAll("^[._-]+|[._-]+$", "");
        if (base.isBlank()) {
            base = "user";
        }
        base = base.substring(0, Math.min(base.length(), 50));

        String candidate = base;
        int suffix = 2;
        while (userRepository.existsByUsername(candidate)) {
            String suffixText = "-" + suffix++;
            candidate = base.substring(0, Math.min(base.length(), 50 - suffixText.length())) + suffixText;
        }
        return candidate;
    }

    @Transactional
    public UserDto updateUser(UUID uuid, List<Map<String, Object>> changes) throws UserNotFoundException, UnsupportedPatchPathException {
        // Buscar el usuario en la base de datos
        User user = userRepository.findById(uuid).orElseThrow( () -> new UserNotFoundException(uuid) );

        // Validar que las operaciones sean válidas
        List<String> invalidPaths = JsonPatchValidation.validatePaths(List.of("/fullName"), changes);

        // Si hay algún invalid path, lanzar una excepción
        if(!invalidPaths.isEmpty()){
            throw new UnsupportedPatchPathException(invalidPaths);
        }

        // Convertir el usuario a su DTO
        UserDto userDto = toDto(user);

        // Aplicar todos los cambios pasados por JSON Patch
        JsonNode patched = patchUtils.applyPatch(mapper.convertValue(userDto, JsonNode.class), changes);

        // Convertir el JSON Node de vuelta al DTO
        User updated = mapper.convertValue(patched, User.class);

        user.setFullName(updated.getFullName());

        // Actualizar el valor del usuario en la BD
        return toDto(userRepository.save(user));
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getUuid(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getCoins(),
                user.getRole()
        );
    }
}
