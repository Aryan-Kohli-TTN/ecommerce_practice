package com.bootcamp.exception.file;

import lombok.Getter;

@Getter
public class FileIsEmptyException extends RuntimeException {
    private final String fileName;
    public FileIsEmptyException(String fileName) {
        super(fileName + "File is Required");
        this.fileName=fileName;
    }
}
