package com.bootcamp.service.impl;

import com.bootcamp.entity.token.BlackListedToken;
import com.bootcamp.repository.token.BlackListedTokenRepository;
import com.bootcamp.service.BlackListedTokenService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class BlackListedTokenServiceImpl implements BlackListedTokenService {
    private final BlackListedTokenRepository blackListedTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(BlackListedTokenServiceImpl.class);


    @Override
    public void blackListToken(String token, Date expiryDate) {
        if (!blackListedTokenRepository.existsByToken(token)) {
            logger.info("Blacklisting token: {}", token);

            BlackListedToken blackListedToken = new BlackListedToken();
            blackListedToken.setToken(token);
            blackListedToken.setExpiryDate(expiryDate);

            blackListedTokenRepository.save(blackListedToken);

            logger.info("Token successfully blacklisted: {}", token);
        } else {
            logger.warn("Token already blacklisted: {}", token);
        }
    }

    @Override
    public boolean isTokenBlackListed(String token){
        return blackListedTokenRepository.existsByToken(token);
    }


}
