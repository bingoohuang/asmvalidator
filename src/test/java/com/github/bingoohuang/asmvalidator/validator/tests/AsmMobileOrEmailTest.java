package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmMobileOrEmail;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import lombok.Data;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AsmMobileOrEmailTest {
    @Data
    public static class AsmMobileOrEmailBean {
        @AsmMobileOrEmail String mobileOrEmail;
    }

    @Test
    public void test1() {
        AsmMobileOrEmailBean bean = new AsmMobileOrEmailBean();
        bean.setMobileOrEmail("18602506990");
        AsmValidatorFactory.validateWithThrow(bean);
    }

    @Test
    public void test2() {
        AsmMobileOrEmailBean bean = new AsmMobileOrEmailBean();
        bean.setMobileOrEmail("bingoo.huang@gmail.com");
        AsmValidatorFactory.validateWithThrow(bean);
    }

    @Test
    public void testBad1() {
        AsmMobileOrEmailBean bean = new AsmMobileOrEmailBean();
        bean.setMobileOrEmail("00602506990");

        try {
            AsmValidatorFactory.validateWithThrow(bean);
            fail();
        } catch (AsmValidateException e) {
            assertTrue(e.getMessage().contains("必须为手机号码或者邮箱"));
        }
    }

    @Test
    public void testBad2() {
        AsmMobileOrEmailBean bean = new AsmMobileOrEmailBean();
        bean.setMobileOrEmail("bingoo.huang@gmail");

        try {
            AsmValidatorFactory.validateWithThrow(bean);
            fail();
        } catch (AsmValidateException e) {
            assertTrue(e.getMessage().contains("必须为手机号码或者邮箱"));
        }
    }
}
