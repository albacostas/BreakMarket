package com.albadiego.breakmarket.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    // Define the default number of coins
    private static final BigDecimal DEFAULT_COINS = BigDecimal.valueOf(1000.00);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal coins;
    @Column(nullable = false)
    private String role;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BetTicket> betTickets;

    @OneToMany(
            mappedBy = "creator",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Bet> betsCreated;

    @OneToMany(
            mappedBy = "author",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Evidence> submittedEvidences;

    @OneToMany(
            mappedBy = "reviewer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Evidence> reviewedEvidences;

    public User() {}

    public User(UUID uuid, String username, String password, String fullName, String email, Long coins, String role) {
        this.uuid = uuid;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.coins = DEFAULT_COINS;
        this.role = role;
    }
}
