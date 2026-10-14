package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.model.Evidence;
import com.albadiego.breakmarket.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/evidence")
@RequiredArgsConstructor
public class EvidenceController {

    private final EvidenceService evidenceService;

    @GetMapping("/{uuid}")
    public ResponseEntity<Evidence> getEvidence(@PathVariable UUID uuid) {
        return ResponseEntity.ok(evidenceService.findById(uuid));
    }

    @GetMapping("/bet/{betUuid}")
    public ResponseEntity<List<Evidence>> getEvidenceByBet(@PathVariable UUID betUuid) {
        return ResponseEntity.ok(evidenceService.findByBet(betUuid));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Evidence>> getPendingEvidence() {
        return ResponseEntity.ok(evidenceService.findPending());
    }

    @PostMapping
    public ResponseEntity<Evidence> submitEvidence(
            @RequestParam UUID betUuid,
            @RequestParam UUID authorUuid,
            @RequestParam String comment) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(evidenceService.submit(betUuid, authorUuid, comment));
    }

    @PatchMapping("/{uuid}/review")
    public ResponseEntity<Evidence> reviewEvidence(
            @PathVariable UUID uuid,
            @RequestParam UUID reviewerUuid,
            @RequestParam String state) {
        return ResponseEntity.ok(evidenceService.review(uuid, reviewerUuid, state));
    }
}
