package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.exceptions.BetNotFoundException;
import com.albadiego.breakmarket.exceptions.BetOptionNotFoundException;
import com.albadiego.breakmarket.exceptions.BetTicketNotFoundException;
import com.albadiego.breakmarket.exceptions.UserNotFoundException;
import com.albadiego.breakmarket.model.Bet;
import com.albadiego.breakmarket.model.BetOption;
import com.albadiego.breakmarket.model.BetTicket;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.BetRepository;
import com.albadiego.breakmarket.repository.BetOptionRepository;
import com.albadiego.breakmarket.repository.BetTicketRepository;
import com.albadiego.breakmarket.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
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
    public List<BetTicket> findByBet(UUID betUuid) {
        requireBet(betUuid);
        return ticketRepository.findByBetUuid(betUuid);
    }

    @Transactional
    public BetTicket create(UUID userUuid, UUID betUuid, UUID optionUuid, Float quantity) {
        User user = requireUser(userUuid);
        Bet bet = requireBet(betUuid);
        BetOption option = optionRepository.findById(optionUuid)
                .orElseThrow(() -> new BetOptionNotFoundException(optionUuid));

        if (!bet.getUuid().equals(option.getBet().getUuid())) {
            throw new IllegalArgumentException("The option does not belong to the bet");
        }
        if (!"OPEN".equalsIgnoreCase(bet.getState())) {
            throw new IllegalStateException("Tickets can only be created for open bets");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        BigDecimal amount = option.getCurrent_rate().multiply(BigDecimal.valueOf(quantity));
        if (user.getCoins().compareTo(amount) < 0) {
            throw new IllegalStateException("User does not have enough coins");
        }

        user.setCoins(user.getCoins().subtract(amount));
        option.setTotal_pool(option.getTotal_pool().add(amount));
        userRepository.save(user);
        optionRepository.save(option);

        BetTicket ticket = new BetTicket();
        ticket.setUser(user);
        ticket.setBet(bet);
        ticket.setOption(option);
        ticket.setRate(option.getCurrent_rate().floatValue());
        ticket.setQuantity(quantity);
        ticket.setState("OPEN");
        ticket.setCreatedDate(Instant.now());
        return ticketRepository.save(ticket);
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
}
