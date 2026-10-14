package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.BetDto;
import com.albadiego.breakmarket.dto.CreateBetDto;
import com.albadiego.breakmarket.exceptions.BetNotFoundException;
import com.albadiego.breakmarket.exceptions.CoupleNotFoundException;
import com.albadiego.breakmarket.exceptions.UserNotFoundException;
import com.albadiego.breakmarket.model.BetOption;
import com.albadiego.breakmarket.model.BetTicket;
import com.albadiego.breakmarket.service.BetOptionService;
import com.albadiego.breakmarket.service.BetService;
import com.albadiego.breakmarket.service.BetTicketService;
import com.albadiego.breakmarket.utils.JsonPatchOperation;
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
    private final BetOptionService betOptionService;
    private final BetTicketService betTicketService;

    @GetMapping
    public ResponseEntity<List<BetDto>> getOpenBets() {
        return ResponseEntity.ok(betService.findAllOpenBets());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<BetDto> getBetById(@PathVariable UUID uuid) throws BetNotFoundException {
        return ResponseEntity.ok(betService.findById(uuid));
    }

    @PostMapping
    public ResponseEntity<BetDto> createBet(@Valid @RequestBody CreateBetDto createBetDto) throws UserNotFoundException, CoupleNotFoundException {
        BetDto created = betService.createBet(createBetDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<BetDto> updateBet(@PathVariable UUID uuid, @RequestBody List<JsonPatchOperation> changes) throws BetNotFoundException {
        return ResponseEntity.ok(betService.updateBet(uuid, changes));
    }

    @GetMapping("/{betUuid}/options")
    public ResponseEntity<List<BetOption>> getBetOptions(@PathVariable UUID betUuid) {
        return ResponseEntity.ok(betOptionService.findByBet(betUuid));
    }

    @PostMapping("/{betUuid}/options")
    public ResponseEntity<BetOption> createBetOption(
            @PathVariable UUID betUuid,
            @RequestParam String description,
            @RequestParam java.math.BigDecimal currentRate) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(betOptionService.create(betUuid, description, currentRate));
    }

    @GetMapping("/{betUuid}/options/{optionUuid}")
    public ResponseEntity<BetOption> getBetOption(@PathVariable UUID optionUuid) {
        return ResponseEntity.ok(betOptionService.findById(optionUuid));
    }

    @PatchMapping("/{betUuid}/options/{optionUuid}")
    public ResponseEntity<BetOption> updateBetOptionRate(
            @PathVariable UUID optionUuid,
            @RequestParam java.math.BigDecimal currentRate) {
        return ResponseEntity.ok(betOptionService.updateRate(optionUuid, currentRate));
    }

    @DeleteMapping("/{betUuid}/options/{optionUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBetOption(@PathVariable UUID optionUuid) {
        betOptionService.delete(optionUuid);
    }

    @GetMapping("/{betUuid}/tickets")
    public ResponseEntity<List<BetTicket>> getBetTickets(@PathVariable UUID betUuid) {
        return ResponseEntity.ok(betTicketService.findByBet(betUuid));
    }

    @PostMapping("/{betUuid}/tickets")
    public ResponseEntity<BetTicket> createBetTicket(
            @PathVariable UUID betUuid,
            @RequestParam UUID userUuid,
            @RequestParam UUID optionUuid,
            @RequestParam Float quantity) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(betTicketService.create(userUuid, betUuid, optionUuid, quantity));
    }

    @GetMapping("/tickets/{ticketUuid}")
    public ResponseEntity<BetTicket> getBetTicket(@PathVariable UUID ticketUuid) {
        return ResponseEntity.ok(betTicketService.findById(ticketUuid));
    }

    @PatchMapping("/tickets/{ticketUuid}/resolve")
    public ResponseEntity<BetTicket> resolveBetTicket(
            @PathVariable UUID ticketUuid,
            @RequestParam String state) {
        return ResponseEntity.ok(betTicketService.resolve(ticketUuid, state));
    }
}