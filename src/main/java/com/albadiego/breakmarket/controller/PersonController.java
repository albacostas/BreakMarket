package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.CreatePersonDto;
import com.albadiego.breakmarket.dto.PersonDto;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/people")
@AllArgsConstructor
public class PersonController {
    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonDto>> getAllPeople() {
        return ResponseEntity.ok(personService.findAllPeople());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<PersonDto> getPersonDetails(@PathVariable UUID uuid) throws PersonNotFoundException {
        return ResponseEntity.ok(personService.findPersonByUuid(uuid));
    }

    @PostMapping
    public ResponseEntity<PersonDto> createPerson(@RequestBody CreatePersonDto newUser) throws PersonAlreadyExistsException, GeneralConstraintViolationException {
        return ResponseEntity.ok(personService.insertPerson(newUser));
    }
}
