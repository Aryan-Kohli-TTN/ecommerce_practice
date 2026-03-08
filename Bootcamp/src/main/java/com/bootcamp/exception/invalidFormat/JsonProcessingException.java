package com.bootcamp.exception.invalidFormat;

public class JsonProcessingException extends RuntimeException {
    public JsonProcessingException() {
        super("Error in processing json");
    }
}
