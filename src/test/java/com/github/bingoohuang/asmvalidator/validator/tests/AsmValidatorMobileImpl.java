package com.github.bingoohuang.asmvalidator.validator.tests;


import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidator;
import com.github.bingoohuang.asmvalidator.ValidateError;
import com.github.bingoohuang.asmvalidator.validator.domain.MobileBean;

public class AsmValidatorMobileImpl implements AsmValidator<MobileBean> {

    @Override
    public AsmValidateResult validate(MobileBean bean) {
        AsmValidateResult result = new AsmValidateResult();
        String mobile = bean.getMobile();

        if (!mobile.matches("^1\\d{10}$")) {
            result.addError(new ValidateError("mobile", mobile, "手机号码格式不正确"));
        }
        return result;
    }


}
