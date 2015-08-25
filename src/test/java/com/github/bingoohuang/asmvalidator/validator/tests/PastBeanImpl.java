package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidator;
import com.github.bingoohuang.asmvalidator.ValidateError;

import java.util.Date;

public class PastBeanImpl implements AsmValidator<AsmPastTest.AsmPastBean1> {
    @Override
    public AsmValidateResult validate(AsmPastTest.AsmPastBean1 var1) {
        AsmValidateResult var2 = new AsmValidateResult();
        this.validateMobiles(var1, var2);
        return var2;
    }

    private void validateMobiles(AsmPastTest.AsmPastBean1 var1, AsmValidateResult var2) {
        Date date = var1.getDate();
        boolean notNull = date != null;
        if (notNull && date.getTime() >= System.currentTimeMillis()) {
            var2.addError(new ValidateError("mobiles", date, "不超过64"));
            return;
        }
    }
    
}
