package com.albadiego.breakmarket.repository;

import com.albadiego.breakmarket.model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, UUID>, JpaSpecificationExecutor<Evidence> {
    List<Evidence> findByBetUuid(UUID betUuid);

    List<Evidence> findByState(String state);
}
