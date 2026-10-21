package com.albadiego.breakmarket.repository;

import com.albadiego.breakmarket.model.Bet;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BetRepository extends JpaRepository<Bet, UUID>, JpaSpecificationExecutor<Bet> {

    // Devuelve todas las apuestas con un estado concreto (ej. "OPEN", "CLOSED")
    @NonNull
    List<Bet> findByState(@NonNull String state);

    // Devuelve las apuestas asociadas a una pareja específica
    @NonNull
    List<Bet> findByCoupleUuid(@NonNull UUID coupleUuid);

    // Devuelve las apuestas creadas por un usuario en particular
    @NonNull
    List<Bet> findByCreatorUuid(@NonNull UUID userUuid);
}