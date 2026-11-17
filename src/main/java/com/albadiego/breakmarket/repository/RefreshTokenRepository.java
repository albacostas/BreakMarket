package com.albadiego.breakmarket.repository;

import com.albadiego.breakmarket.model.BetOption;
import com.albadiego.breakmarket.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenAndIsInvalidatedIsFalse(UUID token, Boolean isInvalidated);
}
