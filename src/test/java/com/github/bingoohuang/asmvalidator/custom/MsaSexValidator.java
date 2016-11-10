package com.github.bingoohuang.asmvalidator.custom;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.MsaValidator;
import com.github.bingoohuang.asmvalidator.ValidateError;

public class MsaSexValidator implements MsaValidator<MsaSex, String> {
    @Override
    public void validate(MsaSex msaSex, AsmValidateResult result, String sex) {
        if ("男".equals(sex) || "女".equals(sex)) return;
        if (msaSex.allowLadyboy() && "人妖".equals(sex)) return;

        result.addError(new ValidateError("sex", sex, "性别非法"));
    }

}
