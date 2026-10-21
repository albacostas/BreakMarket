package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.CreateUserDto;
import com.albadiego.breakmarket.dto.UserDto;
import com.albadiego.breakmarket.exceptions.EmailAlreadyUsedException;
import com.albadiego.breakmarket.exceptions.GeneralConstraintViolationException;
import com.albadiego.breakmarket.exceptions.UserNotFoundException;
import com.albadiego.breakmarket.exceptions.UsernameAlreadyUsedException;
import com.albadiego.breakmarket.model.Person;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.UserRepository;
import com.albadiego.breakmarket.utils.patch.JsonPatch;
import com.albadiego.breakmarket.utils.patch.JsonPatchOperation;
import com.albadiego.breakmarket.utils.specifications.PeopleSpecification;
import com.albadiego.breakmarket.utils.specifications.UserSpecification;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

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

    public UserDto insertUser(CreateUserDto user) throws EmailAlreadyUsedException, UsernameAlreadyUsedException, GeneralConstraintViolationException {
        // Crear el objeto User a partir de los parámetros del usuario
        User newUser = new User();
        newUser.setUsername(user.username());
        newUser.setPassword(user.password());
        newUser.setFullName(user.fullName());
        newUser.setEmail(user.email());
        newUser.setRole(user.role());

        // Insertar el usuario en la BD y coger el objeto creado
        try{
            User createdUser = userRepository.save(newUser);

            // Devolver el nuevo usuario convertido a DTO
            return toDto(createdUser);
        }
        catch (DataIntegrityViolationException e){
            Throwable cause = e.getCause();

            if(cause instanceof ConstraintViolationException constraintException){
                String name = constraintException.getConstraintName();
                if(name != null){
                    if(name.equals("uk6dotkott2kjsp8vw4d0m25fb7")){
                        throw new EmailAlreadyUsedException(user.email());
                    }
                    else if(name.equals("ukr43af9ap4edm43mmtq01oddj6")){
                        throw new UsernameAlreadyUsedException(user.username());
                    }
                }
            }
            throw new GeneralConstraintViolationException(e);
        }
    }

    @Transactional
    public UserDto updateUser(UUID uuid, List<JsonPatchOperation> changes) throws UserNotFoundException {
        // Buscar el usuario en la base de datos
        User user = userRepository.findById(uuid).orElseThrow( () -> new UserNotFoundException(uuid) );

        // Aplicar todos los cambios pasados por JSON Patch
        JsonNode patched = JsonPatch.apply(changes, mapper.convertValue(user, JsonNode.class));

        // Actualizar el valor del usuario en la BD
        User updated = mapper.convertValue(patched, User.class);
        return toDto(userRepository.save(updated));
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getUuid(),
                user.getUsername(),
                user.getPassword(),
                user.getFullName(),
                user.getEmail(),
                user.getCoins(),
                user.getRole()
        );
    }
}
