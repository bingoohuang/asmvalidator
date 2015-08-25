package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidator;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.ValidateError;
import com.github.bingoohuang.asmvalidator.validator.domain.ListBean;

import java.util.List;

public class ListBeanImpl implements AsmValidator<ListBean> {
    @Override
    public AsmValidateResult validate(ListBean var1) {
        AsmValidateResult var2 = new AsmValidateResult();
        this.validateMobiles(var1, var2);
        return var2;
    }

    private void validateMobiles(ListBean var1, AsmValidateResult var2) {
        List mobiles1 = var1.getMobiles();
        boolean notNull = mobiles1 != null;
        if (notNull && mobiles1.size() > 64) {
            var2.addError(new ValidateError("mobiles", mobiles1, "不超过64"));
            return;
        }

        AsmValidatorFactory.validateAll(var1.getMobiles(), var2);
    }

}
