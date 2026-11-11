package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.CoupleDto;
import com.albadiego.breakmarket.dto.CreateCoupleDto;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.service.CoupleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/couples")
@AllArgsConstructor
public class CoupleController {
    private final CoupleService coupleService;

    @GetMapping
    public ResponseEntity<List<CoupleDto>> getCouples(
            @RequestParam(required = false) String firstMember,
            @RequestParam(required = false) String secondMember
    ) {
        return ResponseEntity.ok(coupleService.findCouples(firstMember, secondMember));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CoupleDto> getCoupleDetails(@PathVariable UUID uuid) throws CoupleNotFoundException {
        return ResponseEntity.ok(coupleService.findCoupleByUuid(uuid));
    }

    @PostMapping
    public ResponseEntity<CoupleDto> createCouple(@Valid @RequestBody CreateCoupleDto newCouple) throws PersonNotFoundException, CoupleAlreadyExistsException, GeneralConstraintViolationException {
        return ResponseEntity.ok(coupleService.insertCouple(newCouple));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<CoupleDto> updateCoupleStatus(@PathVariable UUID uuid, @RequestBody List<Map<String, Object>> changes) throws CoupleNotFoundException, UnsupportedPatchPathException, InvalidEnumStateException {
        return ResponseEntity.ok(coupleService.updateCouple(uuid, changes));
    };
}
