package com.github.bingoohuang.asmvalidator.ex;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;

public class AsmValidateException extends RuntimeException {
    private final AsmValidateResult asmValidateResult;

    public AsmValidateException(AsmValidateResult asmValidateResult) {
        this.asmValidateResult = asmValidateResult;
    }

    @Override
    public String getMessage() {
        return asmValidateResult.toString();
    }

    public AsmValidateException replaceFieldName(String oldName, String newName) {
        asmValidateResult.replaceFieldName(oldName, newName);
        return this;
    }
}
