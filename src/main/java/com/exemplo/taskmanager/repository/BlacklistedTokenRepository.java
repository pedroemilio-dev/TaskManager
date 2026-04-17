package com.exemplo.taskmanager.repository;

import com.exemplo.taskmanager.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByToken(String token);
    void deleteByExpiresAtBefore(LocalDateTime now);
}