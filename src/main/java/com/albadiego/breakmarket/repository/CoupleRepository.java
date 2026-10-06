package com.albadiego.breakmarket.repository;

import com.albadiego.breakmarket.model.Couple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CoupleRepository extends JpaRepository<Couple, UUID> {

    // Filtrar parejas activas o que ya han roto
    List<Couple> findByStatus(String status);
}