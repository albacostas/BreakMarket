package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.BetDto;
import com.albadiego.breakmarket.dto.CoupleDto;
import com.albadiego.breakmarket.dto.CreateBetOptionDto;
import com.albadiego.breakmarket.dto.CreateBetDto;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.model.Bet;
import com.albadiego.breakmarket.model.BetOption;
import com.albadiego.breakmarket.model.Couple;
import com.albadiego.breakmarket.model.Person;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.BetRepository;
import com.albadiego.breakmarket.repository.CoupleRepository;
import com.albadiego.breakmarket.repository.PersonRepository;
import com.albadiego.breakmarket.repository.UserRepository;
import com.albadiego.breakmarket.utils.enums.BetState;
import com.albadiego.breakmarket.utils.enums.CoupleState;
import com.albadiego.breakmarket.utils.patch.JsonPatch;
import com.albadiego.breakmarket.utils.patch.JsonPatchOperation;
import com.albadiego.breakmarket.utils.patch.JsonPatchValidation;
import com.albadiego.breakmarket.utils.specifications.BetSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.*;

@AllArgsConstructor
@Service
public class BetService {

    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final PersonRepository personRepository;
    private final JsonMapper mapper;
    private final PatchUtils patchUtils;

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
        bet.setBetOptions(new ArrayList<>());

        Bet saved = betRepository.save(bet);
        if (dto.options() != null) {
            for (CreateBetOptionDto optionDto : dto.options()) {
                BetOption option = new BetOption();
                option.setBet(saved);
                option.setDescription(optionDto.description());
                option.setCurrent_rate(optionDto.currentRate());
                option.setTotal_pool(java.math.BigDecimal.ZERO);
                saved.getBetOptions().add(option);
            }
        }
        if (saved.getBetOptions() != null && !saved.getBetOptions().isEmpty()) {
            betRepository.save(saved);
        }
        return toDto(saved);
    }

    public BetDto updateBet(UUID uuid, List<Map<String, Object>> changes) throws BetNotFoundException, UnsupportedPatchPathException, InvalidEnumStateException {
        // Buscar la apuesta dentro de la base de datos por UUID
        Bet bet = betRepository.findById(uuid).orElseThrow(() -> new BetNotFoundException(uuid));

        // Comprobar que no hay ninguna operación que no sea válida
        List<String> invalidPaths = JsonPatchValidation.validatePaths(List.of("/description", "/state"), changes);
        List<List<String>> invalidStates = JsonPatchValidation.validateState(changes, BetState.class, "/state");

        // Si hay algún invalid path, lanzar una excepción
        if(!invalidPaths.isEmpty()){
            throw new UnsupportedPatchPathException(invalidPaths);
        }

        // Si hay algún estado inválido, lanzar una excepción
        if(!invalidStates.isEmpty()){
            throw new InvalidEnumStateException(invalidStates);
        }

        // Convertir la apuesta a DTO, ya que solo los campos del DTO se pueden modificar
        BetDto betDto = toDto(bet);

        // Aplicar todos los cambios pasados por JSON Patch
        JsonNode patched = patchUtils.applyPatch(mapper.convertValue(betDto, JsonNode.class), changes);

        // Convertir el JSON Node de vuelta a DTO
        BetDto updatedDto = mapper.convertValue(patched, BetDto.class);

        // Aplicar los cambios del DTO al objeto de la BD. Solo se puede modificar la descripción y el estado
        bet.setDescription(updatedDto.description());
        bet.setState(updatedDto.state());

        // Actualizar la apuesta tras aplicar los cambios
        Bet updated = mapper.convertValue(patched, Bet.class);
        Bet saved =  betRepository.save(updated);

        return toDto(saved);
    }

    public List<BetDto> filterBets(
            UUID coupleId,
            UUID authorId,
            String description,
            String type,
            String state,
            Date startDate
    ) {
        // Crear una Specification que combine los parámetros que no sean nulos de todos los posibles
        Specification<Bet> coupleFilter = BetSpecification.betCoupleUuidEquals(coupleId);
        Specification<Bet> descriptionFilter = BetSpecification.betDescriptionEquals(description);
        Specification<Bet> creatorFilter = BetSpecification.betCreatorUuidEquals(authorId);
        Specification<Bet> typeFilter = BetSpecification.betTypeEquals(type);
        Specification<Bet> stateFilter = BetSpecification.betStateEquals(state);
        Specification<Bet> startDateFilter = BetSpecification.betStartDateAfterOrEqual(startDate);

        Specification<Bet> specification = Specification.where(coupleFilter)
                .and(descriptionFilter)
                .and(creatorFilter)
                .and(typeFilter)
                .and(stateFilter)
                .and(startDateFilter);

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