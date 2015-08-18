package com.github.bingoohuang.asmvalidator;

import org.apache.commons.lang3.StringUtils;

public class ValidateError {
    private String fieldName;
    private final String errorMessage;
    private final String fieldValue;

    public ValidateError(String fieldName, String fieldValue, String errorMessage) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ValidatorError{" +
                "fieldName='" + fieldName + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", fieldValue='" + fieldValue + '\'' +
                '}';
    }

    public ValidateError replaceFieldName(String oldName, String newName) {
        if (StringUtils.equals(oldName, fieldName)) fieldName = newName;
        return this;
    }
}
