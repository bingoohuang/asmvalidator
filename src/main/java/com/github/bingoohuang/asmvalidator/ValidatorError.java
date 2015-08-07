package com.github.bingoohuang.asmvalidator;

public class ValidatorError {
    private final String fieldName;
    private final String errorMessage;
    private final String fieldValue;

    public ValidatorError(String fieldName, String fieldValue, String errorMessage) {
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
}
