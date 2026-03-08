package com.bootcamp.exception.category;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MetadataMinCountException extends RuntimeException {
   UUID id;
    public MetadataMinCountException(UUID id) {
        super("");
        this.id=id;

    }
}
