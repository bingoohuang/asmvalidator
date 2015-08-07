package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.domain.MobileBean;
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
