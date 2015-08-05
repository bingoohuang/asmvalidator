package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.domain.Person;
import com.github.bingoohuang.asmvalidator.impl.Consts;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AsmValidatorPersonImpl implements AsmValidator<Person> {
    public AsmValidateResult validate(Person bean) {
        AsmValidateResult result = new AsmValidateResult();
        String name = bean.getName();
        if (isBlank(name)) result.addError(new ValidatorError("name", "不能为空"));

        if (name != null && name.length() > Consts.DEFAULT_MAX_SIZE) {
            result.addError(new ValidatorError("name", "长度超过" + Consts.DEFAULT_MAX_SIZE));
        }

        String addr = bean.getAddr();
        if (isBlank(addr)) result.addError(new ValidatorError("addr", "不能为空"));
        if (name != null && name.length() > Consts.DEFAULT_MAX_SIZE) {
            result.addError(new ValidatorError("addr", "长度超过" + Consts.DEFAULT_MAX_SIZE));
        }

        return result;
    }
}
