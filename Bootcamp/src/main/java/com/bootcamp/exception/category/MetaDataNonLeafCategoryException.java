package com.bootcamp.exception.category;

public class MetaDataNonLeafCategoryException extends RuntimeException {
    public MetaDataNonLeafCategoryException() {
        super("cannot add meta data fields to non leaf categories");
    }
}
