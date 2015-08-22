package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidator;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.validator.domain.ListBean;
import com.github.bingoohuang.asmvalidator.validator.domain.MobileBean;

import java.util.List;

public class ListBeanImpl implements AsmValidator<ListBean> {
    @Override
    public AsmValidateResult validate(ListBean var1) {
        AsmValidateResult var2 = new AsmValidateResult();
        this.validateName(var1, var2);
        this.validateMobiles(var1, var2);
        return var2;
    }

    private void validateMobiles(ListBean var1, AsmValidateResult var2) {
        List<MobileBean> mobiles = var1.getMobiles();
        if (mobiles != null)
            for (MobileBean item : mobiles) {
                AsmValidatorFactory.validate(item, var2);
            }
    }

    private void validateName(ListBean var1, AsmValidateResult var2) {

    }
}
