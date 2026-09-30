package com.albadiego.breakmarket.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "couples")
public class Couple {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @Column(nullable = false)
    private String status;
    @Column(nullable = true)
    private Instant startDate;
    @Column(nullable = true)
    private Instant endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personA_uuid", nullable = false)
    private Person personA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personB_uuid", nullable = false)
    private Person personB;

    @OneToMany(
            mappedBy = "couple",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Bet> betsInvolved;

    public Couple() {}

    public Couple(UUID uuid, String status, Instant startDate, Instant endDate) {
        this.uuid = uuid;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
