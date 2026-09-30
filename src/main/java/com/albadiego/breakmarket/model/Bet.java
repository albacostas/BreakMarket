package com.albadiego.breakmarket.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bets")
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_uuid", nullable = false)
    private Couple couple;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = true)
    private Instant endDate;

    @OneToMany(
            mappedBy = "bet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BetOption> betOptions;

    @OneToMany(
            mappedBy = "bet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BetTicket> betTickets;

    @OneToMany(
            mappedBy = "bet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Evidence> evidences;


}
