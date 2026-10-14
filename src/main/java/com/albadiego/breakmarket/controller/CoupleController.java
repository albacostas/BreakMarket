package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.CoupleDto;
import com.albadiego.breakmarket.dto.CreateCoupleDto;
import com.albadiego.breakmarket.dto.CreateUserDto;
import com.albadiego.breakmarket.dto.UserDto;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.service.CoupleService;
import com.albadiego.breakmarket.service.UserService;
import com.albadiego.breakmarket.utils.JsonPatchOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/couples")
@AllArgsConstructor
public class CoupleController {
    private final CoupleService coupleService;

    @GetMapping
    public ResponseEntity<List<CoupleDto>> getAllUsers() {
        return ResponseEntity.ok(coupleService.findAllCouples());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CoupleDto> getCoupleDetails(@PathVariable UUID uuid) throws CoupleNotFoundException {
        return ResponseEntity.ok(coupleService.findCoupleByUuid(uuid));
    }

    @PostMapping
    public ResponseEntity<CoupleDto> createCouple(@RequestBody CreateCoupleDto newCouple) throws PersonNotFoundException, CoupleAlreadyExistsException, GeneralConstraintViolationException {
        return ResponseEntity.ok(coupleService.insertCouple(newCouple));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<CoupleDto> updateCoupleStatus(@PathVariable UUID uuid, @RequestBody List<JsonPatchOperation> changes) throws CoupleNotFoundException {
        return ResponseEntity.ok(coupleService.updateCouple(uuid, changes));
    };
}
