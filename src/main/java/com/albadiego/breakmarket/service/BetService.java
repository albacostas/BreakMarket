package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.BetDto;
import com.albadiego.breakmarket.dto.CreateBetDto;
import com.albadiego.breakmarket.model.Bet;
import com.albadiego.breakmarket.model.Couple;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.BetRepository;
import com.albadiego.breakmarket.repository.CoupleRepository;
import com.albadiego.breakmarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BetService {

    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;

    @Transactional(readOnly = true)
    public List<BetDto> findAllOpenBets() {
        return betRepository.findByState("OPEN").stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BetDto findById(UUID uuid) {
        return betRepository.findById(uuid)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Apuesta no encontrada: " + uuid));
    }

    @Transactional
    public BetDto createBet(CreateBetDto dto) {
        User creator = userRepository.findById(dto.creatorUuid())
                .orElseThrow(() -> new NoSuchElementException("Usuario creador no encontrado"));

        Couple couple = coupleRepository.findById(dto.coupleUuid())
                .orElseThrow(() -> new NoSuchElementException("Pareja no encontrada"));

        Bet bet = new Bet();
        bet.setCreator(creator);
        bet.setCouple(couple);
        bet.setType(dto.type());
        bet.setDescription(dto.description());
        bet.setState("OPEN");
        bet.setStartDate(Instant.now());
        bet.setEndDate(dto.endDate());

        Bet saved = betRepository.save(bet);
        return toDto(saved);
    }

    private BetDto toDto(Bet bet) {
        return new BetDto(
                bet.getUuid(),
                bet.getCreator().getUuid(),
                bet.getCouple().getUuid(),
                bet.getType(),
                bet.getDescription(),
                bet.getState(),
                bet.getStartDate(),
                bet.getEndDate()
        );
    }
}