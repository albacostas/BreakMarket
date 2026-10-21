package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.BetDto;
import com.albadiego.breakmarket.dto.CreateBetDto;
import com.albadiego.breakmarket.exceptions.BetNotFoundException;
import com.albadiego.breakmarket.exceptions.CoupleNotFoundException;
import com.albadiego.breakmarket.exceptions.UserNotFoundException;
import com.albadiego.breakmarket.model.Bet;
import com.albadiego.breakmarket.model.Couple;
import com.albadiego.breakmarket.model.Person;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.BetRepository;
import com.albadiego.breakmarket.repository.CoupleRepository;
import com.albadiego.breakmarket.repository.PersonRepository;
import com.albadiego.breakmarket.repository.UserRepository;
import com.albadiego.breakmarket.utils.patch.JsonPatch;
import com.albadiego.breakmarket.utils.patch.JsonPatchOperation;
import com.albadiego.breakmarket.utils.specifications.BetSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class BetService {

    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final PersonRepository personRepository;
    private final JsonMapper mapper;

    public BetService(
            BetRepository betRepository,
            UserRepository userRepository,
            CoupleRepository coupleRepository,
            PersonRepository personRepository,
            JsonMapper mapper
    ) {
        this.betRepository = betRepository;
        this.userRepository = userRepository;
        this.coupleRepository = coupleRepository;
        this.personRepository = personRepository;
        this.mapper = mapper;

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
    public BetDto findById(UUID uuid) throws BetNotFoundException {
        return betRepository.findById(uuid)
                .map(this::toDto)
                .orElseThrow(() -> new BetNotFoundException(uuid));
    }

    @Transactional
    public BetDto createBet(CreateBetDto dto) throws UserNotFoundException, CoupleNotFoundException {
        User creator = userRepository.findById(dto.creatorUuid())
                .orElseThrow(() -> new UserNotFoundException(dto.creatorUuid()));

        Couple couple = coupleRepository.findById(dto.coupleUuid())
                .orElseThrow(() -> new CoupleNotFoundException(dto.coupleUuid()));

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

    public BetDto updateBet(UUID uuid, List<JsonPatchOperation> changes) throws BetNotFoundException {
        // Buscar la apuesta dentro de la base de datos por UUID
        Bet bet = betRepository.findById(uuid).orElseThrow(() -> new BetNotFoundException(uuid));

        // Aplicar los cambios de JSON Patch
        JsonNode patched = JsonPatch.apply(changes, mapper.convertValue(bet, JsonNode.class));

        // Actualizar la apuesta tras aplicar los cambios
        Bet updated = mapper.convertValue(patched, Bet.class);
        Bet saved =  betRepository.save(updated);

        return toDto(saved);
    }

    public List<BetDto> filterBets(UUID coupleId, UUID authorId, String description, String type, Date startDate) {
        // Crear una Specification que combine los parámetros que no sean nulos de todos los posibles
        Specification<Bet> coupleFilter = BetSpecification.betCoupleUuidEquals(coupleId);
        Specification<Bet> descriptionFilter = BetSpecification.betDescriptionEquals(description);
        Specification<Bet> creatorFilter = BetSpecification.betCreatorUuidEquals(authorId);
        Specification<Bet> typeFilter = BetSpecification.betTypeEquals(type);

        // TODO - Añadir el filtro de la fecha

        Specification<Bet> specification = Specification.where(coupleFilter).and(descriptionFilter).and(creatorFilter).and(typeFilter);

        // Buscar apuestas dentro de la base de datos dependiendo de los parámetros que se hayan especificado
        List<Bet> results = betRepository.findAll(specification);

        return results
                .stream()
                .map(this::toDto)
                .toList();
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