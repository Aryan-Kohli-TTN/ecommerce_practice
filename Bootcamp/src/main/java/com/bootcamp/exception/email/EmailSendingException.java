package com.bootcamp.exception.email;

public class EmailSendingException extends RuntimeException {
    public EmailSendingException() {
        super("Error in sending mail");
    }
}
