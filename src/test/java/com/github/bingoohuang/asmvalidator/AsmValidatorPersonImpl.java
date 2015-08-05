package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.domain.Person3;
import com.github.bingoohuang.asmvalidator.impl.AsmConsts;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AsmValidatorPersonImpl implements AsmValidator<Person3> {
    public AsmValidateResult validate(Person3 bean) {
        AsmValidateResult result = new AsmValidateResult();
        int intAge = bean.getAge();
        String age = String.valueOf(intAge);
        if (isBlank(age)) result.addError(new ValidatorError("name", "不能为空"));

        if (age != null && age.length() > AsmConsts.DEFAULT_MAX_SIZE) {
            result.addError(new ValidatorError("age", "长度超过" + AsmConsts.DEFAULT_MAX_SIZE));
        }

        if (age == null || age.length() < 127) {
            result.addError(new ValidatorError("age", "长度小于3"));
        }

        String addr = bean.getAddr();
        if (isBlank(addr)) result.addError(new ValidatorError("addr", "不能为空"));
        if (addr != null && addr.length() > AsmConsts.DEFAULT_MAX_SIZE) {
            result.addError(new ValidatorError("addr", "长度超过" + AsmConsts.DEFAULT_MAX_SIZE));
        }

        if (addr == null || addr.length() < 150) {
            result.addError(new ValidatorError("addr", "长度小于3"));
        }

        return result;
    }
}
