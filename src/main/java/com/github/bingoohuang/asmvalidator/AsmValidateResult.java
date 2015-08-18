package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import com.google.common.collect.Lists;

import java.util.List;

public class AsmValidateResult {
    List<ValidateError> errors = Lists.newArrayList();

    public void throwExceptionIfError() {
        if (errors.isEmpty()) return;

        throw new AsmValidateException(this);
    }

    public void addError(ValidateError validateError) {
        errors.add(validateError);
    }

    @Override
    public String toString() {
        return "AsmValidateResult{" +
                "errors=" + errors +
                '}';
    }

    public void addErrors(AsmValidateResult result) {
        errors.addAll(result.getErrors());
    }

    public List<ValidateError> getErrors() {
        return errors;
    }

    public AsmValidateResult replaceFieldName(String oldName, String newName) {
        for (ValidateError validateError : errors) {
            validateError.replaceFieldName(oldName, newName);
        }

        return this;
    }
}
