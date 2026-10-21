package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.BetDto;
import com.albadiego.breakmarket.dto.BetTicketDto;
import com.albadiego.breakmarket.dto.CreateBetDto;
import com.albadiego.breakmarket.exceptions.BetNotFoundException;
import com.albadiego.breakmarket.exceptions.CoupleNotFoundException;
import com.albadiego.breakmarket.exceptions.UserNotFoundException;
import com.albadiego.breakmarket.model.BetOption;
import com.albadiego.breakmarket.model.BetTicket;
import com.albadiego.breakmarket.service.BetOptionService;
import com.albadiego.breakmarket.service.BetService;
import com.albadiego.breakmarket.service.BetTicketService;
import com.albadiego.breakmarket.utils.patch.JsonPatchOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.net.URI;
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
    public ResponseEntity<List<BetDto>> getBets(
            @RequestParam(required = false) UUID coupleId,
            @RequestParam(required = false) UUID authorId,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Date startDate
    ) {
        return ResponseEntity.ok().headers(jsonHeaders())
                .body(betService.filterBets(coupleId, authorId, description, type, startDate));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<BetDto> getBetById(@PathVariable UUID uuid) throws BetNotFoundException {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betService.findById(uuid));
    }

    @PostMapping
    public ResponseEntity<BetDto> createBet(@Valid @RequestBody CreateBetDto createBetDto) throws UserNotFoundException, CoupleNotFoundException {
        BetDto created = betService.createBet(createBetDto);
        return ResponseEntity.created(location(created.uuid()))
                .headers(jsonHeaders())
                .body(created);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<BetDto> updateBet(@PathVariable UUID uuid, @RequestBody List<JsonPatchOperation> changes) throws BetNotFoundException {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betService.updateBet(uuid, changes));
    }

    @GetMapping("/{betUuid}/options")
    public ResponseEntity<List<BetOption>> getBetOptions(@PathVariable UUID betUuid) {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betOptionService.findByBet(betUuid));
    }

    @PostMapping("/{betUuid}/options")
    public ResponseEntity<BetOption> createBetOption(
            @PathVariable UUID betUuid,
            @RequestParam String description,
            @RequestParam java.math.BigDecimal currentRate) {
        BetOption created = betOptionService.create(betUuid, description, currentRate);
        return ResponseEntity.created(location(created.getUuid()))
                .headers(jsonHeaders())
                .body(created);
    }

    @GetMapping("/{betUuid}/options/{optionUuid}")
    public ResponseEntity<BetOption> getBetOption(@PathVariable UUID optionUuid) {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betOptionService.findById(optionUuid));
    }

    @PatchMapping("/{betUuid}/options/{optionUuid}")
    public ResponseEntity<BetOption> updateBetOptionRate(
            @PathVariable UUID optionUuid,
            @RequestParam java.math.BigDecimal currentRate) {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betOptionService.updateRate(optionUuid, currentRate));
    }

    @DeleteMapping("/{betUuid}/options/{optionUuid}")
    public ResponseEntity<Void> deleteBetOption(@PathVariable UUID optionUuid) {
        betOptionService.delete(optionUuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{betUuid}/tickets")
    public ResponseEntity<List<BetTicketDto>> getBetTickets(
            @PathVariable UUID betUuid,
            @RequestParam(required = false) Date date,
            @RequestParam(required = false) UUID authorID,
            @RequestParam(required = false) String option
    ) {
        return ResponseEntity.ok().headers(jsonHeaders())
                .body(betTicketService.findByBet(betUuid, date, authorID, option));
    }

    @PostMapping("/{betUuid}/tickets")
    public ResponseEntity<BetTicket> createBetTicket(
            @PathVariable UUID betUuid,
            @RequestParam UUID userUuid,
            @RequestParam UUID optionUuid,
            @RequestParam Float quantity) {
        BetTicket created = betTicketService.create(userUuid, betUuid, optionUuid, quantity);
        return ResponseEntity.created(location(created.getUuid()))
                .headers(jsonHeaders())
                .body(created);
    }

    @GetMapping("/tickets/{ticketUuid}")
    public ResponseEntity<BetTicket> getBetTicket(@PathVariable UUID ticketUuid) {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betTicketService.findById(ticketUuid));
    }

    @PatchMapping("/tickets/{ticketUuid}/resolve")
    public ResponseEntity<BetTicket> resolveBetTicket(
            @PathVariable UUID ticketUuid,
            @RequestParam String state) {
        return ResponseEntity.ok().headers(jsonHeaders()).body(betTicketService.resolve(ticketUuid, state));
    }

    private static HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private static URI location(UUID uuid) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(uuid)
                .toUri();
    }
}