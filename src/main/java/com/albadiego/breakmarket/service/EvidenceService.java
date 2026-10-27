package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.EvidenceDto;
import com.albadiego.breakmarket.exceptions.BetNotFoundException;
import com.albadiego.breakmarket.exceptions.EvidenceNotFoundException;
import com.albadiego.breakmarket.exceptions.UserNotFoundException;
import com.albadiego.breakmarket.model.Bet;
import com.albadiego.breakmarket.model.Couple;
import com.albadiego.breakmarket.model.Evidence;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.BetRepository;
import com.albadiego.breakmarket.repository.EvidenceRepository;
import com.albadiego.breakmarket.repository.UserRepository;
import com.albadiego.breakmarket.utils.specifications.CoupleSpecification;
import com.albadiego.breakmarket.utils.specifications.EvidenceSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final BetRepository betRepository;
    private final UserRepository userRepository;

    public EvidenceService(
            EvidenceRepository evidenceRepository,
            BetRepository betRepository,
            UserRepository userRepository
    ) {
        this.evidenceRepository = evidenceRepository;
        this.betRepository = betRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<EvidenceDto> findAll(String state, UUID betID, Date submittedDate) {
        // Crear una Specification que combine los parámetros que no sean nulos de todos los posibles
        Specification<Evidence> stateFilter = EvidenceSpecification.evidenceStateEquals(state);
        Specification<Evidence> betIDFilter = EvidenceSpecification.evidenceBetUuidEquals(betID);

        Specification<Evidence> submittedDateFilter =
                EvidenceSpecification.evidenceSubmittedDateAfterOrEqual(submittedDate);

        Specification<Evidence> specification = Specification.where(stateFilter)
                .and(betIDFilter)
                .and(submittedDateFilter);

        List<Evidence> results =  evidenceRepository.findAll(specification);

        // Devolver los resultados como DTO
        return results
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EvidenceDto findById(UUID uuid) {
        return evidenceRepository
                .findById(uuid)
                .map(this::toDto)
                .orElseThrow(() -> new EvidenceNotFoundException(uuid));
    }

    @Transactional(readOnly = true)
    public List<EvidenceDto> findByBet(UUID betUuid) {
        requireBet(betUuid);
        return evidenceRepository
                .findByBetUuid(betUuid)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EvidenceDto> findPending() {
        return evidenceRepository
                .findByState("PENDING")
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public EvidenceDto submit(UUID betUuid, UUID authorUuid, String comment) {
        Bet bet = requireBet(betUuid);
        User author = requireUser(authorUuid);
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Comment is required");
        }

        Evidence evidence = new Evidence();
        evidence.setBet(bet);
        evidence.setAuthor(author);
        evidence.setComment(comment);
        evidence.setSubmittedDate(Instant.now());
        evidence.setState("PENDING");
        return toDto(evidenceRepository.save(evidence));
    }

    @Transactional
    public EvidenceDto review(UUID uuid, UUID reviewerUuid, String state) {
        User reviewer = requireUser(reviewerUuid);
        if (state == null || (!"APPROVED".equalsIgnoreCase(state)
                && !"REJECTED".equalsIgnoreCase(state))) {
            throw new IllegalArgumentException("Review state must be APPROVED or REJECTED");
        }

        // Comprobar si el UUID corresponde a una prueba existente
        Evidence evidence = evidenceRepository
                .findById(uuid)
                .orElseThrow( () -> new EvidenceNotFoundException(uuid));

        evidence.setReviewer(reviewer);
        evidence.setReviewedDate(Instant.now());
        evidence.setState(state.toUpperCase());
        return toDto(evidenceRepository.save(evidence));
    }

    private Bet requireBet(UUID uuid) {
        return betRepository.findById(uuid)
                .orElseThrow(() -> new BetNotFoundException(uuid));
    }

    private User requireUser(UUID uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
    }

    private EvidenceDto toDto(Evidence evidence) {
        return new EvidenceDto(
                evidence.getUuid(),
                evidence.getBet().getUuid(),
                evidence.getAuthor().getUuid(),
                evidence.getComment(),
                evidence.getSubmittedDate(),
                evidence.getState()
        );
    }
}
