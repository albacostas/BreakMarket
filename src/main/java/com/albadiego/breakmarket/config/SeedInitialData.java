package com.albadiego.breakmarket.config;

import com.albadiego.breakmarket.model.Bet;
import com.albadiego.breakmarket.model.BetOption;
import com.albadiego.breakmarket.model.BetTicket;
import com.albadiego.breakmarket.model.Couple;
import com.albadiego.breakmarket.model.Evidence;
import com.albadiego.breakmarket.model.Person;
import com.albadiego.breakmarket.model.User;
import com.albadiego.breakmarket.repository.BetOptionRepository;
import com.albadiego.breakmarket.repository.BetRepository;
import com.albadiego.breakmarket.repository.BetTicketRepository;
import com.albadiego.breakmarket.repository.CoupleRepository;
import com.albadiego.breakmarket.repository.EvidenceRepository;
import com.albadiego.breakmarket.repository.PersonRepository;
import com.albadiego.breakmarket.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Order(1)
public class SeedInitialData implements CommandLineRunner {

    private final BetRepository betRepository;
    private final BetOptionRepository betOptionRepository;
    private final BetTicketRepository betTicketRepository;
    private final EvidenceRepository evidenceRepository;
    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final PersonRepository personRepository;

    public SeedInitialData(
            BetRepository betRepository,
            BetOptionRepository betOptionRepository,
            BetTicketRepository betTicketRepository,
            EvidenceRepository evidenceRepository,
            UserRepository userRepository,
            CoupleRepository coupleRepository,
            PersonRepository personRepository) {
        this.betRepository = betRepository;
        this.betOptionRepository = betOptionRepository;
        this.betTicketRepository = betTicketRepository;
        this.evidenceRepository = evidenceRepository;
        this.userRepository = userRepository;
        this.coupleRepository = coupleRepository;
        this.personRepository = personRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
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

        seedRelatedData(admin);
    }

    private void seedRelatedData(User admin) {
        Bet bet = betRepository.findAll().stream().findFirst().orElse(null);
        if (bet == null) {
            return;
        }

        if (betOptionRepository.findByBetUuid(bet.getUuid()).isEmpty()) {
            BetOption yes = new BetOption();
            yes.setBet(bet);
            yes.setDescription("Sí");
            yes.setCurrent_rate(BigDecimal.valueOf(1.80));
            yes.setTotal_pool(BigDecimal.ZERO);
            betOptionRepository.save(yes);

            BetOption no = new BetOption();
            no.setBet(bet);
            no.setDescription("No");
            no.setCurrent_rate(BigDecimal.valueOf(2.20));
            no.setTotal_pool(BigDecimal.ZERO);
            betOptionRepository.save(no);
        }

        if (betTicketRepository.findByBetUuid(bet.getUuid()).isEmpty()) {
            User tester = userRepository.findAll().stream()
                    .filter(user -> "tester".equals(user.getUsername()))
                    .findFirst()
                    .orElseGet(() -> {
                        User user = new User();
                        user.setUsername("tester");
                        user.setPassword("tester");
                        user.setFullName("Usuario de pruebas");
                        user.setEmail("tester@breakmarket.com");
                        user.setCoins(BigDecimal.valueOf(1000));
                        user.setRole("USER");
                        return userRepository.save(user);
                    });

            BetOption option = betOptionRepository.findByBetUuid(bet.getUuid()).get(0);
            BetTicket ticket = new BetTicket();
            ticket.setUser(tester);
            ticket.setBet(bet);
            ticket.setOption(option);
            ticket.setRate(option.getCurrent_rate().floatValue());
            ticket.setQuantity(10f);
            ticket.setState("OPEN");
            ticket.setCreatedDate(Instant.now());
            betTicketRepository.save(ticket);
        }

        if (evidenceRepository.findByBetUuid(bet.getUuid()).isEmpty()) {
            Evidence evidence = new Evidence();
            evidence.setBet(bet);
            evidence.setAuthor(admin);
            evidence.setComment("Prueba inicial para validar la apuesta.");
            evidence.setSubmittedDate(Instant.now());
            evidence.setState("PENDING");
            evidenceRepository.save(evidence);
        }
    }
}
