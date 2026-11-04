package com.albadiego.breakmarket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "couples")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
