package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import com.github.bingoohuang.asmvalidator.validator.domain.UUIDBean;
import org.junit.Test;

public class AsmUUIDTest {

    @Test(expected = AsmValidateException.class)
    public void testEx() {
        UUIDBean UUIDBean = new UUIDBean();
        UUIDBean.setUuid("123456789");

        AsmValidateResult result = AsmValidatorFactory.validate(UUIDBean);
        result.throwExceptionIfError();
    }

    @Test
    public void testOk() {
        UUIDBean UUIDBean = new UUIDBean();
        UUIDBean.setUuid("87E35233-DD9D-4250-A32C-114BFD456709");
        UUIDBean.setUuid2("87E35233-DD9D-4250-A32C-114BFD456709");

        AsmValidateResult result = AsmValidatorFactory.validate(UUIDBean);
        result.throwExceptionIfError();
    }
}
