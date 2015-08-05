package com.github.bingoohuang.asmvalidator;

import com.google.common.collect.Lists;

import java.util.List;

public class AsmValidateResult {
    List<ValidatorError> errors = Lists.newArrayList();
    public void throwExceptionIfError() {
        if (errors.isEmpty()) return;

        throw new AsmValidatorException(this);
    }

    public void addError(ValidatorError validatorError) {
        errors.add(validatorError);
    }

    @Override
    public String toString() {
        return "AsmValidateResult{" +
                "errors=" + errors +
                '}';
    }
}
