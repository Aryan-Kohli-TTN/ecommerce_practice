package com.bootcamp.exception.auth;

import lombok.Getter;

@Getter
public class ConfirmPasswordNotMatchedException extends RuntimeException {
    String username=null;
    public ConfirmPasswordNotMatchedException() {
        super("Password and confirm password must be same");
    }
    public ConfirmPasswordNotMatchedException(String username) {
        super("Password and confirm password must be same for "+username);
        this.username=username;
    }

}
