package com.albadiego.breakmarket.repository;

import com.albadiego.breakmarket.model.Couple;
import com.albadiego.breakmarket.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

@Repository
public interface CoupleRepository extends JpaRepository<Couple, UUID>, JpaSpecificationExecutor<Couple> {

    // Filtrar parejas activas o que ya han roto
    List<Couple> findByStatus(String status);
}