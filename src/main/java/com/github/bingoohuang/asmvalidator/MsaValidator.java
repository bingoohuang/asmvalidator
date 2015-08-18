package com.github.bingoohuang.asmvalidator;

public interface MsaValidator<A, T> {
    void validate(A asmSex, AsmValidateResult result, T sex);
}
