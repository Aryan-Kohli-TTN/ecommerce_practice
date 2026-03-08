package com.bootcamp.exception.file;

import lombok.Getter;

@Getter
public class FileInvalidFormatException extends RuntimeException {
  String fileName;
    public FileInvalidFormatException(String fileName) {
        super(fileName + " must be in format jpg,jpeg,png,bmp");
        this.fileName=fileName;
    }
}
