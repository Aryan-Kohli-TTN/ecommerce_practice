package com.bootcamp.exception.user;

import lombok.Getter;

@Getter
public class UserAlreadyExistException extends RuntimeException {
    String username=null;
    public UserAlreadyExistException() {
        super("User already exist");
    }
    public  UserAlreadyExistException(String username){
        super(username+" already exist");
        this.username=username;
    }
}
