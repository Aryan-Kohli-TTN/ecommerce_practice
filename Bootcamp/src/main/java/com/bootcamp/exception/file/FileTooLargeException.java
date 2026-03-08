package com.bootcamp.exception.file;

import lombok.Getter;

@Getter
public class FileTooLargeException extends RuntimeException {
    String fileName;
    public FileTooLargeException(String fileName) {
        super(fileName + " must be less than 2mb.");
        this.fileName=fileName;
    }
}
