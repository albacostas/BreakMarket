package com.albadiego.breakmarket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bet_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BetOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bet_uuid", nullable = false)
    private Bet bet;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal current_rate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total_pool;
}
