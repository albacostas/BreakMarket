package com.albadiego.breakmarket.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bet_tickets")
public class BetTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_uuid",
            nullable = false
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bet_uuid",
            nullable = false
    )
    private Bet bet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "option_uuid",
            nullable = false
    )
    private BetOption option;

    @Column(nullable = false)
    private Float rate;

    @Column(nullable = false)
    private Float quantity;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private Instant createdDate;

    @Column(nullable = true)
    private Instant resolvedDate;
}
