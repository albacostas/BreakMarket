package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.CreatePersonDto;
import com.albadiego.breakmarket.dto.CreateUserDto;
import com.albadiego.breakmarket.dto.PersonDto;
import com.albadiego.breakmarket.dto.UserDto;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.model.Person;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.PersonRepository;
import com.albadiego.breakmarket.utils.JsonPatch;
import com.albadiego.breakmarket.utils.JsonPatchOperation;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final ObjectMapper mapper;

    @Transactional(readOnly = true)
    public List<PersonDto> findAllPeople() {
        return personRepository
                .findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PersonDto findPersonByUuid(UUID uuid) throws PersonNotFoundException {
        return personRepository
                .findById(uuid)
                .map(this::toDto)
                .orElseThrow( () -> new PersonNotFoundException(uuid) );
    }
    public PersonDto insertPerson(CreatePersonDto person) throws PersonAlreadyExistsException, GeneralConstraintViolationException {
        // Crear el objeto Person a partir de los parámetros
        Person newPerson = new Person();
        newPerson.setFullName(person.fullName());

        // Insertar el usuario en la BD y coger el objeto creado
        try{
            Person createdPerson = personRepository.save(newPerson);

            // Devolver el nuevo usuario convertido a DTO
            return toDto(createdPerson);
        }
        catch (DataIntegrityViolationException e){
            String constraint = e.getCause().getClass().getName();

            if (constraint.equals("uk48v9lmgy7c2j2wsawwbapaceb")) {
                throw new PersonAlreadyExistsException(newPerson);
            }
            throw new GeneralConstraintViolationException(e);
        }
    }

    private PersonDto toDto(Person person) {
        return new PersonDto(
                person.getUuid(),
                person.getFullName()
        );
    }
}
