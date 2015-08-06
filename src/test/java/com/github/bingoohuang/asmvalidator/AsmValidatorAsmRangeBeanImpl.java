package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.domain.AsmRangeBean;

public class AsmValidatorAsmRangeBeanImpl implements AsmValidator<AsmRangeBean> {
    public AsmValidateResult validate(AsmRangeBean bean) {
        AsmValidateResult result = new AsmValidateResult();
        int intAge = bean.getAge();
        String age = String.valueOf(intAge);
        boolean argNull = age == null;


        if (intAge < 10 || intAge >= 20) {
            result.addError(new ValidatorError("addr", "格式错误"));
        }

        String addr = bean.getAddr();
        if (addr.compareTo("A00") < 0 || addr.compareTo("B99") > 0) {
            result.addError(new ValidatorError("addr", "格式错误"));
        }

        return result;
    }

}
