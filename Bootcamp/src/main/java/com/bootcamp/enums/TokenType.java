package com.bootcamp.enums;

import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS_TOKEN(1000*60*15),
    ACTIVATION_TOKEN(1000*60*60*3),
    REFRESH_TOKEN(1000*60*60*24),
    REFRESH_TOKEN_EXPIRED(0),
    FORGOT_PASSWORD_TOKEN(1000*60*15);
    private final int tokenTime;

    TokenType(int tokenTime) {
        this.tokenTime=tokenTime;
    }
}
