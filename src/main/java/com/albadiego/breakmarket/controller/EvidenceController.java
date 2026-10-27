package com.albadiego.breakmarket.controller;

import com.albadiego.breakmarket.dto.EvidenceDto;
import com.albadiego.breakmarket.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/evidence")
@RequiredArgsConstructor
public class EvidenceController {

    private final EvidenceService evidenceService;

    @GetMapping
    public ResponseEntity<List<EvidenceDto>> getAllEvidences(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) UUID betID,
            @RequestParam(required = false) Date submittedDate
    ) {
        return ResponseEntity.ok().headers(jsonHeaders())
                .body(evidenceService.findAll(state, betID, submittedDate));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<EvidenceDto> getEvidence(@PathVariable UUID uuid) {
        return ResponseEntity.ok().headers(jsonHeaders()).body(evidenceService.findById(uuid));
    }

    @PatchMapping("/{uuid}/review")
    public ResponseEntity<EvidenceDto> reviewEvidence(
            @PathVariable UUID uuid,
            @RequestParam UUID reviewerUuid,
            @RequestParam String state) {
        return ResponseEntity.ok().headers(jsonHeaders()).body(evidenceService.review(uuid, reviewerUuid, state));
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
