package com.github.bingoohuang.asmvalidator;

import com.github.bingoohuang.asmvalidator.domain.AsmRangeBean;
import org.junit.Test;

public class AsmRangeTest {
    @Test
    public void rangOK() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(10);
        bean.setAddr("A00");
        bean.setSex("男");

        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidatorException.class)
    public void rangBad1() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(9);
        bean.setAddr("A00");

        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidatorException.class)
    public void rangBad2() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(11);
        bean.setAddr("000");

        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidatorException.class)
    public void rangBad3() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(11);
        bean.setAddr("B99");

        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidatorException.class)
    public void rangBad4() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(10);
        bean.setAddr("A00");
        bean.setSex("不男不女");

        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }
}
