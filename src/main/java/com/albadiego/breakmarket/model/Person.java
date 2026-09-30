package com.albadiego.breakmarket.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false)
    private String fullName;

    @OneToMany(
            mappedBy = "personA",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Couple> couplesAsPersonA;

    @OneToMany(
            mappedBy = "personB",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Couple> couplesAsPersonB;

}
