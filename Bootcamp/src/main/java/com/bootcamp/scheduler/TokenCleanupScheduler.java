package com.bootcamp.scheduler;

import com.bootcamp.repository.token.BlackListedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class TokenCleanupScheduler {
    private final BlackListedTokenRepository tokenRepository;

    @Autowired
    public TokenCleanupScheduler(BlackListedTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // running scheduler every hour927bc621-c04e-4e07-a315-e06cb4fbd3dc
    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void cleanExpiredTokens(){
        tokenRepository.deleteByExpiryDateBefore(new Date());
    }

}
