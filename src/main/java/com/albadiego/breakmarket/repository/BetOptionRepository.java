package com.albadiego.breakmarket.repository;

import com.albadiego.breakmarket.model.BetOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BetOptionRepository extends JpaRepository<BetOption, UUID> {
    List<BetOption> findByBetUuid(UUID betUuid);
}
