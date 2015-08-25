package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import com.github.bingoohuang.asmvalidator.validator.domain.MobileBean;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MobileBeanTest {

    @Test
    public void testMobile() {
        MobileBean mobileBean = new MobileBean();
        mobileBean.setMobile("12345678900");

        AsmValidateResult result = AsmValidatorFactory.validate(mobileBean);
        result.throwExceptionIfError();
    }

    @Test
    public void testBadMobile() {
        MobileBean mobileBean = new MobileBean();
        mobileBean.setMobile("x12345678900");

        try {
            AsmValidateResult result = AsmValidatorFactory.validate(mobileBean);
            result.throwExceptionIfError();
            fail();
        } catch (AsmValidateException ex) {
            assertTrue(ex.getMessage().contains("手机号码格式非法"));
        }
    }
}
