package com.cbs.AuthService.Repository;

import com.cbs.AuthService.Entity.AuthEntity;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthEntity, Long> {
    Optional<AuthEntity> findByEmail(String email);
    Optional<AuthEntity> findByEntityId(Long entityId);

}
