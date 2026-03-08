package com.bootcamp.repository.token;

import com.bootcamp.entity.token.BlackListedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Repository
public interface BlackListedTokenRepository  extends JpaRepository<BlackListedToken, UUID> {
    boolean existsByToken(String token);
    void deleteByExpiryDateBefore(Date now);
}
