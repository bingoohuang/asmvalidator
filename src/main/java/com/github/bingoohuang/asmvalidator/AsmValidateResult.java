package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
public class AsmValidateResult {
    @Getter List<ValidateError> errors = Lists.newArrayList();

    public void throwExceptionIfError() {
        if (errors.isEmpty()) return;

        throw new AsmValidateException(this);
    }

    public AsmValidateResult addError(ValidateError validateError) {
        errors.add(validateError);
        return this;
    }

    public AsmValidateResult addErrors(AsmValidateResult result) {
        errors.addAll(result.getErrors());
        return this;
    }

    public AsmValidateResult replaceFieldName(String oldName, String newName) {
        for (ValidateError validateError : errors) {
            validateError.replaceFieldName(oldName, newName);
        }

        return this;
    }
}
