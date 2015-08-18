package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidator;
import com.github.bingoohuang.asmvalidator.custom.MsaSex;
import com.github.bingoohuang.asmvalidator.custom.MsaSexValidator;
import com.github.bingoohuang.asmvalidator.utils.AsmConstraintCache;

public class MsaValidatorMsaSexImpl implements AsmValidator<String> {
    @Override
    public AsmValidateResult validate(String bean) {
        AsmValidateResult result = new AsmValidateResult();

        validateUuid(bean, result);

        return result;
    }

    private void validateUuid(String sex, AsmValidateResult result) {
        MsaSex msaSex = (MsaSex) AsmConstraintCache.get("abc123");
        MsaSexValidator sexValidator = new MsaSexValidator();
        sexValidator.validate(msaSex, result, sex);
    }

}
