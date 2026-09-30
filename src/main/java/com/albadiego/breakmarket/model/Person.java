package com.albadiego.breakmarket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
