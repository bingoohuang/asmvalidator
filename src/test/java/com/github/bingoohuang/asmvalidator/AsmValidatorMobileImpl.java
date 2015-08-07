package com.github.bingoohuang.asmvalidator;


import com.github.bingoohuang.asmvalidator.domain.MobileBean;

public class AsmValidatorMobileImpl implements AsmValidator<MobileBean> {

    @Override
    public AsmValidateResult validate(MobileBean bean) {
        AsmValidateResult result = new AsmValidateResult();
        String mobile = bean.getMobile();

        if (!mobile.matches("^1\\d{10}$")) {
            result.addError(new ValidatorError("mobile", "手机号码格式不正确"));
        }
        return result;
    }


}
