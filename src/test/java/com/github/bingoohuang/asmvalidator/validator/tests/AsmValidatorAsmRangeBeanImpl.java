package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidator;
import com.github.bingoohuang.asmvalidator.ValidateError;
import com.github.bingoohuang.asmvalidator.validator.domain.AsmRangeBean;
import com.google.common.collect.Lists;

import java.util.List;

public class AsmValidatorAsmRangeBeanImpl implements AsmValidator<AsmRangeBean> {
    public AsmValidateResult validate(AsmRangeBean bean) {
        AsmValidateResult result = new AsmValidateResult();
        int intAge = bean.getAge();
        String age = String.valueOf(intAge);
        boolean argNull = age == null;


        if (intAge < 10) {
            result.addError(new ValidateError("age", age, "格式错误"));
        }

        if (intAge > 20) {
            result.addError(new ValidateError("age", age, "格式错误"));
        }

        String addr = bean.getAddr();
        if (addr.compareTo("A00") < 0) {
            result.addError(new ValidateError("addr", addr, "格式错误"));
        }
        if (addr.compareTo("B99") > 0) {
            result.addError(new ValidateError("addr", addr, "格式错误"));
        }

        String sex = bean.getSex();
        List<String> sexList = Lists.newArrayList("男", "女", "人妖");
        if (!sexList.contains(sex)) {
            result.addError(new ValidateError("sex", sex, "格式错误"));
        }

        int ageMin = bean.getAgeMin();
        if (ageMin < 10) {
            result.addError(new ValidateError("age", sex, "格式错误"));
        }


        return result;
    }

}
