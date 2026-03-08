package com.bootcamp.service;

import java.util.Date;

public interface BlackListedTokenService {
    void blackListToken(String token, Date expiryDate);

    boolean isTokenBlackListed(String token);
}
