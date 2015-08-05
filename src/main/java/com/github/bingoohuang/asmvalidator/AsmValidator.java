package com.github.bingoohuang.asmvalidator;

public interface AsmValidator<T> {
    AsmValidateResult validate(T bean);
}
