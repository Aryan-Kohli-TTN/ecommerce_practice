package com.bootcamp.exception.file;

import lombok.Getter;

@Getter
public class FileSavingException extends RuntimeException {
    public FileSavingException() {
        super("Error in saving the image");
    }
}
