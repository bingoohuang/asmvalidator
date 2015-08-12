package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.validator.domain.MobileBean;
import org.junit.Test;

public class MobileBeanTest {

    @Test
    public void testMobile() {
        MobileBean mobileBean = new MobileBean();
        mobileBean.setMobile("12345678900");

        AsmValidateResult result = AsmValidatorFactory.validate(mobileBean);
        result.throwExceptionIfError();
    }
}
