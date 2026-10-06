package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.BetDto;
import com.albadiego.breakmarket.dto.CreateBetDto;
import com.albadiego.breakmarket.service.BetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bets")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;

    @GetMapping
    public ResponseEntity<List<BetDto>> getOpenBets() {
        return ResponseEntity.ok(betService.findAllOpenBets());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<BetDto> getBetById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(betService.findById(uuid));
    }

    @PostMapping
    public ResponseEntity<BetDto> createBet(@Valid @RequestBody CreateBetDto createBetDto) {
        BetDto created = betService.createBet(createBetDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}