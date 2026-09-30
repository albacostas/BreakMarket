package com.albadiego.breakmarket.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Evidence {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bet_uuid", nullable = false)
    private Bet bet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = true)
    private User reviewer;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private Instant submittedDate;

    @Column(nullable = true)
    private Instant reviewedDate;

    @Column(nullable = false)
    private String state;
}
