package com.github.bingoohuang.asmvalidator;

public interface MsaValidator<A, T> {
    void validate(A annotation, AsmValidateResult result, T value);
}
