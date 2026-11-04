package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.dto.BetTicketDto;
import com.albadiego.breakmarket.dto.CreateBetTicketDto;
import com.albadiego.breakmarket.exceptions.*;
import com.albadiego.breakmarket.model.Bet;
import com.albadiego.breakmarket.model.BetOption;
import com.albadiego.breakmarket.model.BetTicket;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.BetRepository;
import com.albadiego.breakmarket.repository.BetOptionRepository;
import com.albadiego.breakmarket.repository.BetTicketRepository;
import com.albadiego.breakmarket.repository.UserRepository;
import com.albadiego.breakmarket.utils.enums.BetState;
import com.albadiego.breakmarket.utils.specifications.BetSpecification;
import com.albadiego.breakmarket.utils.specifications.TicketSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class BetTicketService {
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final BetOptionRepository optionRepository;
    private final BetTicketRepository ticketRepository;

    public BetTicketService(
            BetRepository betRepository,
            UserRepository userRepository,
            BetOptionRepository optionRepository,
            BetTicketRepository ticketRepository
    ){
        this.betRepository = betRepository;
        this.userRepository = userRepository;
        this.optionRepository = optionRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    public BetTicket findById(UUID uuid) {
        return ticketRepository.findById(uuid)
                .orElseThrow(() -> new BetTicketNotFoundException(uuid));
    }

    @Transactional(readOnly = true)
    public List<BetTicket> findByUser(UUID userUuid) {
        requireUser(userUuid);
        return ticketRepository.findByUserUuid(userUuid);
    }

    @Transactional(readOnly = true)
    public List<BetTicketDto> findByBet(UUID betUuid, Date date, UUID authorID, String option) {
        // Obligar a que se especifique un bet UUID
        requireBet(betUuid);

        // Crear una Specification que combine los parámetros que no sean nulos de todos los posibles
        Specification<BetTicket> authorFilter = TicketSpecification.ticketUserUuidEquals(authorID);
        Specification<BetTicket> optionFilter = TicketSpecification.ticketOptionTypeEquals(option);

        // TODO - Añadir el filtro de la fecha

        Specification<BetTicket> specification = Specification.where(authorFilter).and(optionFilter);

        List<BetTicket> results =  ticketRepository.findAll(specification);

        // Devolver los resultados como DTO
        return results
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public BetTicketDto create(UUID betUuid, CreateBetTicketDto createBetTicketDto) throws BetOptionNotFoundException, BetClosedException, InvalidBetTicketQuantityException, NotEnoughCoinsException {
        // Comprobar que la apuesta existe
        Bet bet = requireBet(betUuid);

        // Comprobar que el usuario existe
        User user = requireUser(createBetTicketDto.userUUID());

        // Comprobar que la opción existe dentro de la apuesta
        BetOption selectedOption = null;
        for (BetOption option: bet.getBetOptions()) {
            if(option.getUuid().equals(createBetTicketDto.optionUUID())) {
                selectedOption = option;
                break;
            }
        }

        if(selectedOption == null) {
            throw new BetOptionNotFoundException(createBetTicketDto.optionUUID());
        }

        // Comprobar que el estado de la Bet es OPEN
        if(!bet.getState().equals(BetState.ACTIVE)) {
            throw new BetClosedException(bet.getUuid());
        }

        // Comprobar que la cantidad especificada es
        Integer quantity = createBetTicketDto.quantity();
        if (quantity == null || quantity <= 0) {
            throw new InvalidBetTicketQuantityException(quantity);
        }

        // Comprobar que el usuario tiene fondos suficientes para realizar la apuesta
        BigDecimal totalAmount = selectedOption.getCurrentRate().multiply(BigDecimal.valueOf(quantity));
        if (user.getCoins().compareTo(totalAmount) < 0) {
            throw new NotEnoughCoinsException(user.getCoins(), totalAmount);
        }

        // Actualizar el número de monedas del usuario
        user.setCoins(user.getCoins().subtract(totalAmount));
        userRepository.save(user);

        // Crear el ticket para el usuario
        BetTicket ticket = new BetTicket();
        ticket.setUser(user);
        ticket.setBet(bet);
        ticket.setOption(selectedOption);
        ticket.setRate(selectedOption.getCurrentRate().floatValue());
        ticket.setQuantity(quantity);
        ticket.setState("OPEN");
        ticket.setCreatedDate(Instant.now());

        // Guardar el ticket en la base de datos
        BetTicket createdTicket = ticketRepository.save(ticket);

        // Actualizar el número de opciones asociadas a una opción
        selectedOption.setTotalPool(selectedOption.getTotalPool() + quantity);

        // Obtener el número total de options
        Integer totalOptions = bet.getBetOptions().stream().map(BetOption::getTotalPool).reduce(0, Integer::sum);

        // Actualizar los rates de todas las opciones
        for (BetOption option: bet.getBetOptions()) {
            option.setCurrentRate(BigDecimal.valueOf(option.getTotalPool().doubleValue()/totalOptions.doubleValue()));
        }

        // Guardar la apuesta modificada
        betRepository.save(bet);

        // Devolver el ticket creado
        return toDto(createdTicket);
    }

    @Transactional
    public BetTicket resolve(UUID uuid, String state) {
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State is required");
        }
        BetTicket ticket = findById(uuid);
        ticket.setState(state);
        ticket.setResolvedDate(Instant.now());
        return ticketRepository.save(ticket);
    }

    private User requireUser(UUID uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
    }

    private Bet requireBet(UUID uuid) {
        return betRepository.findById(uuid)
                .orElseThrow(() -> new BetNotFoundException(uuid));
    }

    private BetTicketDto toDto(BetTicket ticket) {
        return new BetTicketDto(
                ticket.getUuid(),
                ticket.getUser().getUuid(),
                ticket.getBet().getUuid(),
                ticket.getOption().getUuid(),
                ticket.getRate(),
                ticket.getQuantity(),
                ticket.getState(),
                ticket.getCreatedDate()
        );
    }
}
