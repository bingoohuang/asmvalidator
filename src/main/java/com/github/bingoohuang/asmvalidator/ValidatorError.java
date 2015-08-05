package com.github.bingoohuang.asmvalidator;

public class ValidatorError {
    private final String fieldName;
    private final String errorMessage;

    public ValidatorError(String fieldName, String errorMessage) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ValidatorError{" +
                "fieldName='" + fieldName + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
