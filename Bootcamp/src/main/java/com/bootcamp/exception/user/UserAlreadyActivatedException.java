package com.bootcamp.exception.user;

public class UserAlreadyActivatedException extends RuntimeException {
    public UserAlreadyActivatedException() {
        super("User Already Activated");
    }
}
