package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.*;
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
import com.albadiego.breakmarket.utils.enums.BetType;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    public BetDto createBet(CreateBetDto dto) throws UserNotFoundException, CoupleNotFoundException, BetTypeNotSupportedException, InvalidBetOptionDateException, BetOptionsNotSpecifiedException {
        User creator = userRepository.findById(dto.creatorUuid())
                .orElseThrow(() -> new UserNotFoundException(dto.creatorUuid()));

        Couple couple = coupleRepository.findById(dto.coupleUuid())
                .orElseThrow(() -> new CoupleNotFoundException(dto.coupleUuid()));

        // Comprobar que el tipo de apuesta especificado es válido
        BetType betType = null;
        try {
            betType = Enum.valueOf(BetType.class, dto.type());
        } catch (IllegalArgumentException e) {
            // Si no es válido, lanzar una excepción
            throw new BetTypeNotSupportedException(dto.type());
        }

        Bet bet = new Bet();
        bet.setCreator(creator);
        bet.setCouple(couple);
        bet.setType(dto.type());
        bet.setDescription(dto.description());
        bet.setState(BetState.ACTIVE);
        bet.setStartDate(Instant.now());
        bet.setEndDate(dto.endDate());

        // Crear las opciones dependiendo del tipo de apuesta
        if(betType.equals(BetType.DATE)) {
            // Comprobar si las opciones son válidas o no
            if(dto.options() == null) {
                throw new BetOptionsNotSpecifiedException();
            }

            List<LocalDate> validDates = new ArrayList<>();
            List<String> invalidDates = new ArrayList<>();

            for(String date : dto.options()) {
                try {
                    validDates.add(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                } catch (DateTimeParseException e) {
                    invalidDates.add(date);
                }
            }

            // Comprobar si alguna fecha es inválida
            if(!invalidDates.isEmpty()) {
                throw new InvalidBetOptionDateException(invalidDates);
            }

            // Procesar como opciones todas las opciones válidas
            int numberOfOptions = validDates.size();
            List<BetOption> betOptionList = new ArrayList<>();
            for(LocalDate date : validDates) {
                // Crear la opción para esta persona
                BetOption betOption = new BetOption();
                betOption.setBet(bet);
                betOption.setDescription(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                betOption.setCurrentRate(BigDecimal.valueOf(1.0/numberOfOptions));
                betOption.setTotalPool(1);

                // Añadir la opción a la lista
                betOptionList.add(betOption);
            }

            bet.setBetOptions(betOptionList);
        }
        else if(betType.equals(BetType.PERSON)) {
            // Comprobar si las opciones son válidas o no
            if(dto.options() == null) {
                throw new BetOptionsNotSpecifiedException();
            }

            int numberOfOptions = dto.options().size();
            List<BetOption> betOptionList = new ArrayList<>();

            for(String person : dto.options()) {
                // Crear la opción para esta persona
                BetOption betOption = new BetOption();
                betOption.setBet(bet);
                betOption.setDescription(person);
                betOption.setCurrentRate(BigDecimal.valueOf(1.0/numberOfOptions));
                betOption.setTotalPool(1);

                // Añadir la opción a la lista
                betOptionList.add(betOption);
            }

            bet.setBetOptions(betOptionList);
        }
        else if(betType.equals(BetType.YES_NO)) {
            // Crear la apuesta para SÍ
            BetOption betOptionYes = new BetOption();
            betOptionYes.setBet(bet);
            betOptionYes.setDescription("Sí");
            betOptionYes.setCurrentRate(BigDecimal.valueOf(0.5));
            betOptionYes.setTotalPool(1);

            // Crear la opción para NO
            BetOption betOptionNo = new BetOption();
            betOptionNo.setBet(bet);
            betOptionNo.setDescription("No");
            betOptionNo.setCurrentRate(BigDecimal.valueOf(0.5));
            betOptionNo.setTotalPool(1);

            bet.setBetOptions(List.of(betOptionYes, betOptionNo));
        }
        else {
            throw new BetTypeNotSupportedException(dto.type());
        }

        // Guardar la apuesta creada
        Bet saved = betRepository.save(bet);
        return toDto(saved);
    }

    public List<BetOptionDto> findOptionsByBet(UUID betUuid) throws BetNotFoundException {
        // Buscar la apuesta dentro de la base de datos por UUID
        Bet bet = betRepository.findById(betUuid).orElseThrow(() -> new BetNotFoundException(betUuid));

        // Parsear las opciones de la apuesta
        List<BetOptionDto> betOptions = bet.getBetOptions().stream().map(this::toDto).toList();

        // Devolver la lista de opciones
        return betOptions;
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

    private BetOptionDto toDto(BetOption betOption) {
        return new BetOptionDto(
                betOption.getUuid(),
                betOption.getDescription(),
                betOption.getCurrentRate(),
                betOption.getTotalPool()
        );
    }
}