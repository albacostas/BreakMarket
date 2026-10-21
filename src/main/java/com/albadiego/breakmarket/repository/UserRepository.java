package com.albadiego.breakmarket.repository;

import com.albadiego.breakmarket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    // Para comprobar logins o validar duplicados
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}