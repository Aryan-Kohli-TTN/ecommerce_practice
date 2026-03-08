package com.bootcamp.exception.category;

import java.util.List;
import java.util.UUID;

public class SomeMetaFieldsExistException extends RuntimeException {
    UUID fieldId;
  List<String> duplicateValues;
    public SomeMetaFieldsExistException(UUID fieldId, List<String> duplicateValues) {
        super("some meta field already exist");
        this.fieldId=fieldId;
        this.duplicateValues=duplicateValues;
    }

  public String getDuplicateValues() {
    return String.join(",",duplicateValues);
  }

  public UUID getFieldId() {
    return fieldId;
  }
}
