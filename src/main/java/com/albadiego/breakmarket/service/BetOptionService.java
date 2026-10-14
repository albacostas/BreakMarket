package com.albadiego.breakmarket.service;

import com.albadiego.breakmarket.exceptions.BetNotFoundException;
import com.albadiego.breakmarket.exceptions.BetOptionNotFoundException;
import com.albadiego.breakmarket.model.Bet;
import com.albadiego.breakmarket.model.BetOption;
import com.albadiego.breakmarket.repository.BetOptionRepository;
import com.albadiego.breakmarket.repository.BetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class BetOptionService {

    private final BetOptionRepository optionRepository;
    private final BetRepository betRepository;

    public BetOptionService(BetOptionRepository optionRepository, BetRepository betRepository) {
        this.optionRepository = optionRepository;
        this.betRepository = betRepository;
    }

    @Transactional(readOnly = true)
    public List<BetOption> findByBet(UUID betUuid) {
        requireBet(betUuid);
        return optionRepository.findByBetUuid(betUuid);
    }

    @Transactional(readOnly = true)
    public BetOption findById(UUID uuid) {
        return optionRepository.findById(uuid)
                .orElseThrow(() -> new BetOptionNotFoundException(uuid));
    }

    @Transactional
    public BetOption create(UUID betUuid, String description, BigDecimal currentRate) {
        Bet bet = requireBet(betUuid);
        if (description == null || description.isBlank() || currentRate == null
                || currentRate.signum() <= 0) {
            throw new IllegalArgumentException("Description and a positive rate are required");
        }

        BetOption option = new BetOption();
        option.setBet(bet);
        option.setDescription(description);
        option.setCurrent_rate(currentRate);
        option.setTotal_pool(BigDecimal.ZERO);
        return optionRepository.save(option);
    }

    @Transactional
    public BetOption updateRate(UUID uuid, BigDecimal currentRate) {
        if (currentRate == null || currentRate.signum() <= 0) {
            throw new IllegalArgumentException("Rate must be positive");
        }
        BetOption option = findById(uuid);
        option.setCurrent_rate(currentRate);
        return optionRepository.save(option);
    }

    @Transactional
    public void delete(UUID uuid) {
        optionRepository.delete(findById(uuid));
    }

    private Bet requireBet(UUID uuid) {
        return betRepository.findById(uuid)
                .orElseThrow(() -> new BetNotFoundException(uuid));
    }
}
