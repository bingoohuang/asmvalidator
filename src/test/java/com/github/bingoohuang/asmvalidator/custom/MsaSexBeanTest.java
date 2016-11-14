package com.github.bingoohuang.asmvalidator.custom;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmBlankable;
import com.github.bingoohuang.asmvalidator.annotations.AsmNotBlank;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

public class MsaSexBeanTest {
    @Data @AllArgsConstructor public static class SexBean {
        @AsmBlankable @MsaSex private String sex;
        @AsmNotBlank @MsaSex(allowLadyboy = true) private String sex2;
    }

    @Test public void validMale() {
        SexBean sexBean = new SexBean("男", "女");
        AsmValidatorFactory.validateWithThrow(sexBean);
    }

    @Test public void validFemale() {
        SexBean sexBean = new SexBean("女", "女");
        AsmValidatorFactory.validateWithThrow(sexBean);
    }

    @Test public void validLadyBody() {
        SexBean sexBean = new SexBean("男", "人妖");
        AsmValidatorFactory.validateWithThrow(sexBean);
    }

    @Test(expected = AsmValidateException.class)
    public void validBadLadyBaoy() {
        SexBean sexBean = new SexBean("人妖", "人妖");
        AsmValidatorFactory.validateWithThrow(sexBean);
    }

    @Test(expected = AsmValidateException.class) public void bad() {
        SexBean sexBean = new SexBean("X", "Y");
        AsmValidatorFactory.validateWithThrow(sexBean);
    }

    @Test public void nullSex() {
        SexBean sexBean = new SexBean(null, "人妖");
        AsmValidatorFactory.validateWithThrow(sexBean);
    }
}
