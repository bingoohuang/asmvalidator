package com.github.bingoohuang.asmvalidator.validator.tests;


import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.ValidateError;
import com.github.bingoohuang.asmvalidator.validation.AsmBase64ValidateGenerator;

public class AsmValidatorBase64Impl {

    public void validate(String str) {
        AsmValidateResult result = new AsmValidateResult();

        String padded = str;
        padded = AsmBase64ValidateGenerator.padding(padded);

        if (!padded.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$")) {
            result.addError(new ValidateError("mobile", str, "手机号码格式不正确"));
            return;
        }
    }

}


