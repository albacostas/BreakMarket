package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.*;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.model.Couple;
import com.albadiego.breakmarket.model.Person;
import com.albadiego.breakmarket.repository.CoupleRepository;
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
public class CoupleService {
    private final CoupleRepository coupleRepository;
    private final PersonRepository personRepository;
    private final ObjectMapper mapper;

    @Transactional(readOnly = true)
    public List<CoupleDto> findAllCouples() {
        return coupleRepository
                .findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CoupleDto findCoupleByUuid(UUID uuid) throws CoupleNotFoundException {
        return coupleRepository
                .findById(uuid)
                .map(this::toDto)
                .orElseThrow( () -> new CoupleNotFoundException(uuid) );
    }

    public CoupleDto insertCouple(CreateCoupleDto couple) throws PersonNotFoundException, CoupleAlreadyExistsException, GeneralConstraintViolationException {
        // Buscar las Personas integrantes de la pareja
        Person personA = personRepository.findById(couple.members().get(0))
                .orElseThrow( () -> new PersonNotFoundException(couple.members().get(0)));
        Person personB = personRepository.findById(couple.members().get(1))
                .orElseThrow( () -> new PersonNotFoundException(couple.members().get(1)));

        // Crear el objeto Couple a partir de los parámetros
        Couple newCouple = new Couple();
        newCouple.setStatus(couple.status());
        newCouple.setStartDate(couple.startDate());
        newCouple.setPersonA(personA);
        newCouple.setPersonB(personB);

        // Insertar la pareja en la BD y coger el objeto creado
        try{
            Couple createdCouple = coupleRepository.save(newCouple);

            // Devolver el nuevo usuario convertido a DTO
            return toDto(createdCouple);
        }
        catch (DataIntegrityViolationException e){
            String constraint = e.getCause().getClass().getName();
            System.err.println("EXCEPTION: " + constraint + "\n\n\n");

            if (constraint.equals("uk6dotkott2kjsp8vw4d0m25fb7")) {
                throw new CoupleAlreadyExistsException(personA, personB);
            }
            throw new GeneralConstraintViolationException(e);

        }
    }

    @Transactional
    public CoupleDto updateCouple(UUID uuid, List<JsonPatchOperation> changes) throws CoupleNotFoundException {
        // Buscar la pareja en la base de datos
        Couple couple = coupleRepository.findById(uuid).orElseThrow( () -> new CoupleNotFoundException(uuid) );

        // Aplicar todos los cambios pasados por JSON Patch
        JsonNode patched = JsonPatch.apply(changes, mapper.convertValue(couple, JsonNode.class));

        // Actualizar el valor del usuario en la BD
        Couple updated = mapper.convertValue(patched, Couple.class);
        return toDto(coupleRepository.save(updated));
    }

    private CoupleDto toDto(Couple couple) {
        List<Person> coupleMembers = List.of(couple.getPersonA(), couple.getPersonB());

        return new CoupleDto(
                couple.getUuid(),
                couple.getStatus(),
                couple.getStartDate(),
                couple.getEndDate(),
                coupleMembers.stream().map( member ->
                        new PersonDto(member.getUuid(), member.getFullName())
                ).toList()
        );
    }
}
