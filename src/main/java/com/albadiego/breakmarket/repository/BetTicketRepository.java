package com.albadiego.breakmarket.repository;

import com.albadiego.breakmarket.model.BetTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BetTicketRepository extends JpaRepository<BetTicket, UUID>, JpaSpecificationExecutor<BetTicket> {
    List<BetTicket> findByUserUuid(UUID userUuid);

    List<BetTicket> findByBetUuid(UUID betUuid);
}
