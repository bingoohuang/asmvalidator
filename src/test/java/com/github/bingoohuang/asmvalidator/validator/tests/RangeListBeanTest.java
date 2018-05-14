package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import com.github.bingoohuang.asmvalidator.validator.domain.MobileBean;
import com.google.common.collect.Lists;
import lombok.Data;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.createValidatorSignature;
import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.validate;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RangeListBeanTest {
    @Test
    public void test() {
        ListBean listBean = new ListBean();
        listBean.setName("bingoo");
        ArrayList<MobileBean> strings = Lists.newArrayList(new MobileBean("18602506990"));
        listBean.setMobiles(strings);
        AsmValidatorFactory.validateWithThrow(listBean);
    }

    @Test
    public void test2() {
        ListBean listBean = new ListBean();
        listBean.setName("bingoo");
        ArrayList<MobileBean> strings = Lists.newArrayList(new MobileBean("X8602506990"));
        listBean.setMobiles(strings);

        try {
            AsmValidatorFactory.validateWithThrow(listBean);
        } catch (AsmValidateException ex) {
            assertTrue(ex.getMessage().contains("手机号码格式非法"));
        }
    }

    public interface ListParams {
        @AsmValid
        String something(List<MobileBean> mobiles);
    }

    @Test
    public void validMobile() {
        Method method = ListParams.class.getMethods()[0];
        AsmParamsValidatorFactory.createValidators(method);
        String validatorSignature = createValidatorSignature(method);
        ArrayList<MobileBean> strings = Lists.newArrayList(new MobileBean("18602506990"));

        validate(validatorSignature, strings);
    }

    @Test
    public void invalidMobile() {
        Method method = ListParams.class.getMethods()[0];
        AsmParamsValidatorFactory.createValidators(method);
        String validatorSignature = createValidatorSignature(method);
        ArrayList<MobileBean> strings = Lists.newArrayList(new MobileBean("X8602506990"));

        try {
            validate(validatorSignature, strings);
            fail();
        } catch (AsmValidateException ex) {
            assertTrue(ex.getMessage().contains("手机号码格式非法"));
        }
    }

    @Data
    public static class ListBean {
        @AsmIgnore private String name;
        private List<MobileBean> mobiles;
    }
}
