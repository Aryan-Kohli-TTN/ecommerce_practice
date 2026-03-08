package com.bootcamp.exception.file;

import lombok.Getter;

@Getter
public class FileRetervingException extends RuntimeException {
    public FileRetervingException() {
        super("Error in reterving the image");
    }
}
