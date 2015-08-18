package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.validator.domain.AsmRangeBean;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import org.junit.Test;

public class AsmRangeTest {
    @Test
    public void rangOK() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(10);
        bean.setAddr("A00");
        bean.setSex("男");
        bean.setRmb(10);
        bean.setAgeMax(10);
        bean.setAgeMin(10);
        bean.setCode("A00");
        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void rangBad1() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(9);
        bean.setAddr("A00");
        bean.setSex("男");
        bean.setRmb(10);
        bean.setAgeMin(10);
        bean.setAgeMax(10);
        bean.setCode("A00");

        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void rangBad2() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(11);
        bean.setAddr("000");
        bean.setRmb(10);
        bean.setSex("男");
        bean.setAgeMin(10);
        bean.setAgeMax(10);
        bean.setCode("A00");

        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void rangBad3() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(11);
        bean.setAddr("B99");
        bean.setRmb(10);
        bean.setSex("男");
        bean.setAgeMin(10);
        bean.setAgeMax(10);
        bean.setCode("A00");

        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void rangBad4() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(10);
        bean.setAddr("A00");
        bean.setSex("不男不女");
        bean.setAgeMin(10);
        bean.setAgeMax(10);
        bean.setCode("A00");

        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void rangeBad5() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(10);
        bean.setAddr("A00");
        bean.setSex("男");
        bean.setRmb(165);
        bean.setAgeMin(10);
        bean.setAgeMax(10);
        bean.setCode("A00");
        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void rangeBad6() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(10);
        bean.setAddr("A00");
        bean.setSex("男");
        bean.setRmb(100);
        bean.setAgeMin(9);
        bean.setCode("A00");
        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void rangeBad7() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(10);
        bean.setAddr("A00");
        bean.setSex("男");
        bean.setRmb(100);
        bean.setAgeMin(10);
        bean.setAgeMax(11);
        bean.setCode("A00");
        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test(expected = AsmValidateException.class)
    public void rangeBad8() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(10);
        bean.setAddr("A00");
        bean.setSex("男");
        bean.setRmb(100);
        bean.setAgeMin(10);
        bean.setAgeMax(100);
        bean.setCode("A00");
        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test()
    public void rangeBad9() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(10);
        bean.setAddr("A00");
        bean.setSex("男");
        bean.setRmb(100);
        bean.setAgeMin(10);
        bean.setAgeMax(10);
        bean.setCode("A01");
        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }

    @Test
    public void rangeBad10() {
        AsmRangeBean bean = new AsmRangeBean();
        bean.setAge(10);
        bean.setAddr("A00");
        bean.setSex("男");
        bean.setRmb(100);
        bean.setAgeMin(10);
        bean.setAgeMax(10);
        bean.setCode("A01");
        AsmValidateResult result = AsmValidatorFactory.validate(bean);
        result.throwExceptionIfError();
    }


}
