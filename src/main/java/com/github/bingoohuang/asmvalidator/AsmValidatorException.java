package com.github.bingoohuang.asmvalidator;

public class AsmValidatorException extends RuntimeException {
    private final AsmValidateResult asmValidateResult;

    public AsmValidatorException(AsmValidateResult asmValidateResult) {
        this.asmValidateResult = asmValidateResult;
    }

    @Override
    public String getMessage() {
        return asmValidateResult.toString();
    }
}
