package com.bootcamp.exception.category;

import java.util.List;
import java.util.UUID;

public class DuplicateMetaDataValuesException extends  RuntimeException{
     List<String> duplicateValues;
     UUID fieldID;
    public DuplicateMetaDataValuesException(List<String> duplicateValues, UUID fieldId) {
        super("");
        this.duplicateValues=duplicateValues;
        this.fieldID=fieldId;
    }

    public UUID getFieldID() {
        return fieldID;
    }

    public String getDuplicateValues() {
        return String.join(",",duplicateValues);
    }
}
