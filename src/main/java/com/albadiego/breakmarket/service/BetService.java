package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.BetDto;
import com.albadiego.breakmarket.dto.CreateBetDto;
import com.albadiego.breakmarket.model.Bet;
import com.albadiego.breakmarket.model.Couple;
import com.albadiego.breakmarket.model.Person;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.BetRepository;
import com.albadiego.breakmarket.repository.CoupleRepository;
import com.albadiego.breakmarket.repository.PersonRepository;
import com.albadiego.breakmarket.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BetService {

    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final PersonRepository personRepository;

    public BetService(
            BetRepository betRepository,
            UserRepository userRepository,
            CoupleRepository coupleRepository,
            PersonRepository personRepository
    ) {
        this.betRepository = betRepository;
        this.userRepository = userRepository;
        this.coupleRepository = coupleRepository;
        this.personRepository = personRepository;

        seedInitialData();
    }

    private void seedInitialData() {
        User admin = userRepository.findByEmail("admin@breakmarket.com")
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername("admin");
                    user.setPassword("admin");
                    user.setFullName("Administrador");
                    user.setEmail("admin@breakmarket.com");
                    user.setCoins(BigDecimal.valueOf(1000));
                    user.setRole("ADMIN");
                    return userRepository.save(user);
                });

        if (coupleRepository.count() == 0) {
            Person personA = new Person();
            personA.setFullName("Ana García");
            personA = personRepository.save(personA);

            Person personB = new Person();
            personB.setFullName("Luis Pérez");
            personB = personRepository.save(personB);

            Couple couple = new Couple();
            couple.setStatus("ACTIVE");
            couple.setStartDate(Instant.now());
            couple.setPersonA(personA);
            couple.setPersonB(personB);
            couple = coupleRepository.save(couple);

            Bet bet = new Bet();
            bet.setCreator(admin);
            bet.setCouple(couple);
            bet.setType("RELATIONSHIP");
            bet.setDescription("¿Seguirán Ana y Luis juntos al finalizar el año?");
            bet.setState("OPEN");
            bet.setStartDate(Instant.now());
            bet.setEndDate(Instant.now().plus(30, ChronoUnit.DAYS));

            betRepository.save(bet);
        }
    }

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