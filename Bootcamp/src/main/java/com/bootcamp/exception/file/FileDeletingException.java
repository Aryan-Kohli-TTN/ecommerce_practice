package com.bootcamp.exception.file;

import lombok.Getter;

@Getter
public class FileDeletingException extends RuntimeException {
    public FileDeletingException() {
        super("Error in delete the image");
    }
}
